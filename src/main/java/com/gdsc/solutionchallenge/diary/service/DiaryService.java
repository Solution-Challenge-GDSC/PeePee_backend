package com.gdsc.solutionchallenge.diary.service;

import com.gdsc.solutionchallenge.diary.dto.DiaryReq;
import com.gdsc.solutionchallenge.diary.dto.DiaryRes;
import com.gdsc.solutionchallenge.diary.entity.Diary;
import com.gdsc.solutionchallenge.diary.entity.DiaryPhoto;
import com.gdsc.solutionchallenge.diary.repository.DiaryPhotoRepository;
import com.gdsc.solutionchallenge.diary.repository.DiaryRepository;
import com.gdsc.solutionchallenge.global.exception.ApiException;
import com.gdsc.solutionchallenge.global.exception.ApiResponseStatus;
import com.gdsc.solutionchallenge.global.image.DiaryImageUploadService;
import com.gdsc.solutionchallenge.global.image.GetGDSRes;
import com.gdsc.solutionchallenge.global.image.BoardImageUploadService;
import com.gdsc.solutionchallenge.user.entity.User;
import com.gdsc.solutionchallenge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.gdsc.solutionchallenge.global.exception.ApiResponseStatus.DATABASE_ERROR;
import static com.gdsc.solutionchallenge.global.exception.ApiResponseStatus.USER_WITHOUT_PERMISSION;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final DiaryPhotoRepository diaryPhotoRepository;
    private final UserRepository userRepository;
    private final DiaryImageUploadService diaryImageUploadService;
    private final DiaryPhotoService diaryPhotoService;
    private final Logger logger = LoggerFactory.getLogger(DiaryService.class);

    @Transactional
    public String createDiary(String email, DiaryReq.postDiaryReq postDiaryReq, List<MultipartFile> multipartFiles) {
        try {
            // 블랙 유저 검증
            // reportService.checkBlackUser("board",userId);

            User user = userRepository.findByEmail(email).orElseThrow(() -> {
                throw new ApiException(ApiResponseStatus.NONE_EXIST_USER);
            });

            Diary diary = Diary.builder()
                    .content(postDiaryReq.getContent())
                    .mood(postDiaryReq.getMood())
                    .date(postDiaryReq.getDate())
                    .user(user)
                    .diaryPhotos(new ArrayList<>())
                    .isOpened(postDiaryReq.isOpened())
                    .build();
            diaryRepository.save(diary);

            //TODO: 구글 클라우드에 사진 저장
            if (multipartFiles != null) {
                List<GetGDSRes> getS3ResList = diaryImageUploadService.uploadImage(multipartFiles);
                diaryPhotoService.saveAllPostPhotoByDiary(getS3ResList, diary);
            }


            return "diaryId: " + diary.getDiaryId() + "인 다이어리를 생성했습니다.";
        } catch (ApiException exception) {
            throw new ApiException(exception.getStatus());
        }
    }


    @Transactional
    public DiaryRes.GetDiaryResWrapper getDiaryByDate(String email, String date) {
        try {
            Diary diaryOne = diaryRepository.findByDateAndUserEmail(date, email).orElse(null);
            List<DiaryRes.GetDiaryRes> otherDiariesList = new ArrayList<>();

            if (diaryOne != null) {
                // 내가 작성한 다이어리가 있는 경우
                // 내가 작성한 다이어리 정보 가져오기
                List<DiaryPhoto> diaryPhotos = diaryPhotoRepository.findAllBydiaryId(diaryOne.getDiaryId()).orElse(Collections.emptyList());
                List<GetGDSRes> getGDSRes = diaryPhotos.stream()
                        .map(photo -> new GetGDSRes(photo.getImgUrl(), photo.getFileName()))
                        .collect(Collectors.toList());

                // 내가 작성한 다이어리 정보를 GetDiaryRes 객체로 생성
                DiaryRes.GetDiaryRes myDiary = new DiaryRes.GetDiaryRes(diaryOne.getDiaryId(), diaryOne.getContent(),
                        diaryOne.getMood(), diaryOne.isOpened(), diaryOne.getDate(), getGDSRes);

                // 내가 작성한 다이어리를 제외한 5개의 최신 다이어리 가져오기
                List<Diary> otherDiaries = diaryRepository.findTop5ByIsOpenedAndUserEmailNot(email);
                otherDiariesList = otherDiaries.stream()
                        .map(diary -> {
                            List<DiaryPhoto> diaryPhotosOther = diaryPhotoRepository.findAllBydiaryId(diary.getDiaryId()).orElse(Collections.emptyList());
                            List<GetGDSRes> getGDSResOther = diaryPhotosOther.stream()
                                    .map(photo -> new GetGDSRes(photo.getImgUrl(), photo.getFileName()))
                                    .collect(Collectors.toList());
                            return new DiaryRes.GetDiaryRes(diary.getDiaryId(), diary.getContent(),
                                    diary.getMood(), diary.isOpened(), diary.getDate(), getGDSResOther);
                        })
                        .collect(Collectors.toList());
                return new DiaryRes.GetDiaryResWrapper(myDiary, otherDiariesList);
            } else {
                // 내가 작성한 다이어리가 없는 경우
                // 내가 작성한 다이어리를 제외한 5개의 최신 다이어리만 가져오기
                List<Diary> otherDiaries = diaryRepository.findTop5ByIsOpenedAndUserEmailNot(email);
                otherDiariesList = otherDiaries.stream()
                        .map(diary -> {
                            List<DiaryPhoto> diaryPhotosOther = diaryPhotoRepository.findAllBydiaryId(diary.getDiaryId()).orElse(Collections.emptyList());
                            List<GetGDSRes> getGDSResOther = diaryPhotosOther.stream()
                                    .map(photo -> new GetGDSRes(photo.getImgUrl(), photo.getFileName()))
                                    .collect(Collectors.toList());
                            return new DiaryRes.GetDiaryRes(diary.getDiaryId(), diary.getContent(),
                                    diary.getMood(), diary.isOpened(), diary.getDate(), getGDSResOther);
                        })
                        .collect(Collectors.toList());
                return new DiaryRes.GetDiaryResWrapper(null, otherDiariesList);
            }
        } catch (Exception exception) {
            throw new ApiException(DATABASE_ERROR);
        }
    }




    @Transactional
    public String deleteDiary(String email, Long diaryId) {

        Diary diary = diaryRepository.findById(diaryId).orElseThrow(() -> {
            throw new ApiException(ApiResponseStatus.NOT_EXIST_POST);
        });

        Long writerId = diaryRepository.findById(diaryId).get().getUser().getUserId();
        Long visitorId = userRepository.findByEmail(email).get().getUserId();

        //TODO: 업로드한 게시글 사진들 삭제
        if (writerId == visitorId) {
            // S3에 업로드된 파일을 삭제하는 명령
            List<DiaryPhoto> allByDiaryId = diaryPhotoService.findAllByDiaryId(diaryId);
            if (!allByDiaryId.isEmpty()) {
                diaryPhotoService.deleteAllPostPhotos(allByDiaryId);
                diaryPhotoRepository.deletePostPhotoByBoardId(diaryId);
            }
            // 게시글을 삭제하는 명령
            diaryRepository.delete(diary);
            return "요청하신 다이어리에 대한 삭제가 완료되었습니다.";
        } else {
            throw new ApiException(USER_WITHOUT_PERMISSION);
        }
    }

    @Transactional
    public String modifyDiary(String email, DiaryReq.patchDiaryReq patchDiaryReq,
                              List<MultipartFile> multipartFiles) {
        try {

            Diary diary = diaryRepository.findById(patchDiaryReq.getDiaryId()).orElseThrow(() -> {
                throw new ApiException(ApiResponseStatus.NOT_EXIST_POST);
            });

            User writer = diaryRepository.findById(patchDiaryReq.getDiaryId()).get().getUser();
            User visitor = userRepository.findByEmail(email).get();

            if (writer.getUserId() == visitor.getUserId()) { // 만약 접근자가 다이어리 작성자와 일치하면
                diary.updateDiary(patchDiaryReq.getContent(), patchDiaryReq.getMood(), patchDiaryReq.isOpened());
                //TODO: 사진 업데이트, 지우고 다시 저장
                List<DiaryPhoto> allByDiaryId = diaryPhotoService.findAllByDiaryId(diary.getDiaryId());
                diaryPhotoService.deleteAllPostPhotos(allByDiaryId);
                List<Long> ids = diaryPhotoService.findAllId(diary.getDiaryId());
                diaryPhotoService.deleteAllDiaryPhotoByDiary(ids);

                if (multipartFiles != null) {
                    List<GetGDSRes> getS3ResList = diaryImageUploadService.uploadImage(multipartFiles);
                    diaryPhotoService.saveAllPostPhotoByDiary(getS3ResList, diary);
                }
                return "diaryId " + diary.getDiaryId() + "의 다이어리를 수정했습니다.";
            } else {
                throw new ApiException(USER_WITHOUT_PERMISSION);
            }
        } catch (ApiException exception) {
            logger.info(exception.getMessage());
            throw new ApiException(exception.getStatus());
        }
    }

}
