package com.gdsc.solutionchallenge.meetup.service;

import com.gdsc.solutionchallenge.global.image.BoardImageUploadService;
import com.gdsc.solutionchallenge.global.image.GetGDSRes;
import com.gdsc.solutionchallenge.meetup.entity.Meetup;
import com.gdsc.solutionchallenge.meetup.entity.MeetupPhoto;
import com.gdsc.solutionchallenge.meetup.repository.MeetupPhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetupPhotoService {

    private final MeetupPhotoRepository meetupPhotoRepository;
    private final BoardImageUploadService imageUploadService;

    @Transactional
    public void saveMeetupPhoto(List<MeetupPhoto> meetupPhotos){
        meetupPhotoRepository.saveAll(meetupPhotos);
    }

    /**
     *  여러 개의 PostPhoto 저장
     */
    @Transactional
    public void saveAllMeetupPhotoByMeetup(List<GetGDSRes> getGDSResList , Meetup meetup) {
        // MeetupPhoto 리스트를 받아옴
        List<MeetupPhoto> meetupPhotos = new ArrayList<>();
        for (GetGDSRes getGDSRes : getGDSResList) {
            MeetupPhoto newMeetupPhoto = MeetupPhoto.builder()
                    .imgUrl(getGDSRes.getImgUrl())
                    .fileName(getGDSRes.getFileName())
                    .build();
            meetupPhotos.add(newMeetupPhoto);
            meetup.addPhotoList(newMeetupPhoto);
        }
        saveMeetupPhoto(meetupPhotos);
    }

    /**
     * 게시글과 연관된 모든 postPhoto 삭제
     */
    @Transactional
    public void deleteAllMeetupPhotoByMeetup(List<Long> ids){
        meetupPhotoRepository.deleteAllByMeetup(ids);
    }

    @Transactional
    public void deleteAllMeetupPhotos(List<MeetupPhoto> meetupPhotos){
        for (MeetupPhoto meetupPhoto  : meetupPhotos) {
            imageUploadService.deleteMeetupImage(meetupPhoto.getFileName());
        }
    }

    /**
     * 게시글과 연관된 모든 postPhoto 의 imgUrl 조회
     */
    public List<String> findAllPhotosByMeetupId(Long meetupId){
        return meetupPhotoRepository.findAllPhotos(meetupId);
    }

    /**
     * 게시글와 연관된 모든 id 조회
     */
    public List<Long> findAllId(Long meetupId){
        return meetupPhotoRepository.findAllId(meetupId);
    }

    public List<MeetupPhoto> findAllByMeetupId(Long meetupId){
        return meetupPhotoRepository.findAllByMeetupId(meetupId).orElse(null);
    }

    /**
     * 썸네일 적용을 위한 메서드
     */
    public String findFirstByPostId(Long meetupId) {
        List<MeetupPhoto> meetupPhotos = meetupPhotoRepository.findAllByMeetupId(meetupId).orElse(null);

        if(meetupPhotos.size() == 0) {
            return "첨부된 사진이 없습니다.";
        } else {
            return meetupPhotos.get(0).getImgUrl();
        }
    }
}
