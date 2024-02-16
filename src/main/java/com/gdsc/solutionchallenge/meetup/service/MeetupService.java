package com.gdsc.solutionchallenge.meetup.service;

import com.gdsc.solutionchallenge.board.dto.BoardRes;
import com.gdsc.solutionchallenge.board.entity.Board;
import com.gdsc.solutionchallenge.board.entity.PostPhoto;
import com.gdsc.solutionchallenge.global.exception.ApiException;
import com.gdsc.solutionchallenge.global.exception.ApiResponseStatus;
import com.gdsc.solutionchallenge.global.image.BoardImageUploadService;
import com.gdsc.solutionchallenge.global.image.GetGDSRes;
import com.gdsc.solutionchallenge.meetup.GeometryUtils;
import com.gdsc.solutionchallenge.meetup.dto.LocationDto;
import com.gdsc.solutionchallenge.meetup.dto.MeetupReq;
import com.gdsc.solutionchallenge.meetup.dto.MeetupRes;
import com.gdsc.solutionchallenge.meetup.entity.Meetup;
import com.gdsc.solutionchallenge.meetup.entity.MeetupPhoto;
import com.gdsc.solutionchallenge.meetup.repository.MeetupPhotoRepository;
import com.gdsc.solutionchallenge.meetup.repository.MeetupRepository;
import com.gdsc.solutionchallenge.user.entity.User;
import com.gdsc.solutionchallenge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.gdsc.solutionchallenge.board.service.BoardService.convertLocalDateTimeToLocalDate;
import static com.gdsc.solutionchallenge.board.service.BoardService.convertLocalDateTimeToTime;
import static com.gdsc.solutionchallenge.global.exception.ApiResponseStatus.DATABASE_ERROR;
import static com.gdsc.solutionchallenge.global.exception.ApiResponseStatus.USER_WITHOUT_PERMISSION;

@Service
@RequiredArgsConstructor
public class MeetupService {

    private final MeetupRepository meetupRepository;
    private final UserRepository userRepository;
    private final MeetupPhotoRepository meetupPhotoRepository;
    private final MeetupPhotoService meetupPhotoService;
    private final BoardImageUploadService imageUploadService;

    public List<MeetupRes.GetMeetupRes> findMeetupsWithinDistance(LocationDto.Post userLocation) {
        List<MeetupRes.GetMeetupRes> meetupsWithinDistance = new ArrayList<>();

        List<Meetup> allMeetups = meetupRepository.findAll();

        for (Meetup meetup : allMeetups) {
            // 사용자 위치와 Meetup 위치 사이의 거리 계산
            double distance = GeometryUtils.calculateDistanceHaversine(
                    userLocation.getLatitude(), userLocation.getLongitude(),
                    meetup.getLatitude(), meetup.getLongitude());

            // 거리가 3000m 이내인 meetup을 결과 리스트에 추가
            if (distance <= 3000) {
                MeetupRes.GetMeetupRes getMeetupRes = new MeetupRes.GetMeetupRes(
                        meetup.getMeetupId(),
                        convertLocalDateTimeToLocalDate(meetup.getCreatedDate()),
                        meetup.getActivityDay(),
                        meetup.getContent(),
                        meetup.getUser().getNickname(),
                        meetup.getParents(),
                        meetup.getBaby(),
                        meetup.getLatitude(),
                        meetup.getLongitude()

                );
                meetupsWithinDistance.add(getMeetupRes);
            }
        }

        return meetupsWithinDistance;
    }

    @Transactional
    public String createMeetup(String email, MeetupReq.PostMeetupReq postMeetupReq, List<MultipartFile> multipartFiles) {
        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> {
                throw new ApiException(ApiResponseStatus.NONE_EXIST_USER);
            });

            Meetup meetup = Meetup.builder()
                    .content(postMeetupReq.getContent())
                    .photoList(new ArrayList<>())
                    .activityDay(postMeetupReq.getActivityDay())
                    .parents(postMeetupReq.getParents())
                    .baby(postMeetupReq.getBaby())
                    .latitude(postMeetupReq.getLatitude())
                    .longitude(postMeetupReq.getLongitude())
                    .user(user)
                    .build();
            meetupRepository.save(meetup);

            //TODO: 구글 클라우드에 사진 저장
            if (multipartFiles != null) {
                List<GetGDSRes> getS3ResList = imageUploadService.uploadImage(multipartFiles);
                meetupPhotoService.saveAllMeetupPhotoByMeetup(getS3ResList, meetup);
            }

            return "meetupId: " + meetup.getMeetupId() + "인 게시글을 생성했습니다.";
        } catch (ApiException exception) {
            throw new ApiException(exception.getStatus());
        }
    }

    @Transactional
    public MeetupRes.GetMeetupDetailRes getMeetupByMeetupId(Long meetupId) {
        Meetup meetup = meetupRepository.findById(meetupId).orElseThrow(() -> {
            throw new ApiException(ApiResponseStatus.NOT_EXIST_POST);
        });
        List<MeetupPhoto> meetupPhotos = meetupPhotoRepository.findAllByMeetupId(meetupId).orElse(Collections.emptyList());


        List<GetGDSRes> getGDSRes = meetupPhotos.stream()
                .map(photo -> new GetGDSRes(photo.getImgUrl(), photo.getFileName()))
                .collect(Collectors.toList());
        String profile = null;
        if (meetup.getUser().getProfileImage() != null) {
            profile = meetup.getUser().getProfileImage();
        }


        MeetupRes.GetMeetupDetailRes getMeetupDetailRes = new MeetupRes.GetMeetupDetailRes(meetup.getMeetupId(),
                convertLocalDateTimeToLocalDate(meetup.getCreatedDate()), convertLocalDateTimeToTime(meetup.getCreatedDate()),
                meetup.getActivityDay(), meetup.getUser().getNickname(), meetup.getParents(), meetup.getBaby(),
                meetup.getUser().getProfileImage(), meetup.getContent(), getGDSRes);

        return getMeetupDetailRes;
    }

    @Transactional
    public List<MeetupRes.GetMeetupRes> getMeetupByUserId(String email) {
        try {
            List<Meetup> meetups = meetupRepository.findBoardByUser_EmailOrderByMeetupIdDesc(email);
            List<MeetupRes.GetMeetupRes> getMeetupRes = meetups.stream()
                    .map(meetup -> new MeetupRes.GetMeetupRes(meetup.getMeetupId(),
                            convertLocalDateTimeToLocalDate(meetup.getCreatedDate()),
                            convertLocalDateTimeToTime(meetup.getCreatedDate()),
                            meetup.getUser().getNickname(), meetup.getContent(),
                            meetup.getParents(), meetup.getBaby(),
                            meetup.getLatitude(), meetup.getLongitude()
                    ))
                    .collect(Collectors.toList());
            return getMeetupRes;
        } catch (Exception exception) {
            throw new ApiException(DATABASE_ERROR);
        }
    }

    @Transactional
    public String deleteMeetup(String email, Long meetupId) {

        Meetup meetup = meetupRepository.findById(meetupId).orElseThrow(() -> {
            throw new ApiException(ApiResponseStatus.NOT_EXIST_POST);
        });
        Long writerId = meetupRepository.findById(meetupId).get().getUser().getUserId();
        Long visitorId = userRepository.findByEmail(email).get().getUserId();

        //TODO: 업로드한 게시글 사진들 삭제
        if (writerId == visitorId) {
            // S3에 업로드된 파일을 삭제하는 명령
            List<MeetupPhoto> allByMeetupId = meetupPhotoService.findAllByMeetupId(meetupId);
            if (!allByMeetupId.isEmpty()) {
                meetupPhotoService.deleteAllMeetupPhotos(allByMeetupId);
                meetupPhotoRepository.deleteMeetupPhotoByMeetupId(meetupId);
            }
            // 게시글을 삭제하는 명령
            meetupRepository.delete(meetup);
            return "요청하신 게시글에 대한 삭제가 완료되었습니다.";
        } else {
            throw new ApiException(USER_WITHOUT_PERMISSION);
        }
    }

    @Transactional
    public String modifyMeetup(String email, MeetupReq.PatchMeetupReq patchMeetupReq,
                               List<MultipartFile> multipartFiles) {
        try {
            Long meetupId = patchMeetupReq.getMeetupId();

            Meetup meetup = meetupRepository.findById(meetupId).orElseThrow(() -> {
                throw new ApiException(ApiResponseStatus.NOT_EXIST_POST);
            });
            User writer = meetupRepository.findById(meetupId).get().getUser();
            User visitor = userRepository.findByEmail(email).get();
            if (writer.getUserId() == visitor.getUserId()) {
                meetup.updateMeetup(patchMeetupReq.getContent(), patchMeetupReq.getActivityDay()
                        , patchMeetupReq.getParents(), patchMeetupReq.getBaby()
                        , patchMeetupReq.getLatitude(), patchMeetupReq.getLongitude());
                //TODO: 사진 업데이트, 지우고 다시 저장
                List<MeetupPhoto> allByMeetupId = meetupPhotoService.findAllByMeetupId(meetupId);
                meetupPhotoService.deleteAllMeetupPhotos(allByMeetupId);
                List<Long> ids = meetupPhotoService.findAllId(meetup.getMeetupId());
                meetupPhotoService.deleteAllMeetupPhotoByMeetup(ids);

                if (multipartFiles != null) {
                    List<GetGDSRes> getS3ResList = imageUploadService.uploadImage(multipartFiles);
                    meetupPhotoService.saveAllMeetupPhotoByMeetup(getS3ResList, meetup);
                }
                return "meetupId " + meetup.getMeetupId() + "의 게시글을 수정했습니다.";
            } else {
                throw new ApiException(USER_WITHOUT_PERMISSION);
            }
        } catch (ApiException exception) {
            throw new ApiException(exception.getStatus());
        }
    }

    public Meetup findById(Long meetupId) {
        return meetupRepository.findById(meetupId).orElse(null);
    }
}