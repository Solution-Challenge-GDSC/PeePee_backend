package com.gdsc.solutionchallenge.diary.service;


import com.gdsc.solutionchallenge.diary.entity.Diary;
import com.gdsc.solutionchallenge.diary.entity.DiaryPhoto;
import com.gdsc.solutionchallenge.diary.repository.DiaryPhotoRepository;
import com.gdsc.solutionchallenge.global.image.DiaryImageUploadService;
import com.gdsc.solutionchallenge.global.image.GetGDSRes;
import com.gdsc.solutionchallenge.global.image.BoardImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryPhotoService {

    private final DiaryPhotoRepository diaryPhotoRepository;
    private final DiaryImageUploadService diaryImageUploadService;

    @Transactional
    public void savePostPhoto(List<DiaryPhoto> diaryPhotos){
        diaryPhotoRepository.saveAll(diaryPhotos);
    }

    /**
     *  여러 개의 PostPhoto 저장
     */
    @Transactional
    public void saveAllPostPhotoByDiary(List<GetGDSRes> getGDSResList , Diary diary) {
        // DiaryPhoto 리스트를 받아옴
        List<DiaryPhoto> diaryPhotos = new ArrayList<>();
        for (GetGDSRes getGDSRes : getGDSResList) {
            DiaryPhoto newPostPhoto = DiaryPhoto.builder()
                    .imgUrl(getGDSRes.getImgUrl())
                    .fileName(getGDSRes.getFileName())
                    .build();
            diaryPhotos.add(newPostPhoto);
            diary.addPhotoList(newPostPhoto);
        }
        savePostPhoto(diaryPhotos);
    }

    /**
     * 게시글과 연관된 모든 postPhoto 삭제
     */
    @Transactional
    public void deleteAllDiaryPhotoByDiary(List<Long> ids){
        diaryPhotoRepository.deleteAllByDiary(ids);
    }

    @Transactional
    public void deleteAllPostPhotos(List<DiaryPhoto> diaryPhotos){
        for (DiaryPhoto diaryPhoto : diaryPhotos) {
            diaryImageUploadService.deleteImage(diaryPhoto.getFileName());
        }
    }

    /**
     * 게시글과 연관된 모든 postPhoto 의 imgUrl 조회
     */
    public List<String> findAllPhotosByPostId(Long diaryId){
        return diaryPhotoRepository.findAllPhotos(diaryId);
    }

    /**
     * 게시글와 연관된 모든 id 조회
     */
    public List<Long> findAllId(Long diaryId){
        return diaryPhotoRepository.findAllId(diaryId);
    }

    public List<DiaryPhoto> findAllByDiaryId(Long diaryId){
        return diaryPhotoRepository.findAllBydiaryId(diaryId).orElse(null);
    }

    /**
     * 썸네일 적용을 위한 메서드
     */
    public String findFirstByPostId(Long diaryId) {
        List<DiaryPhoto> diaryPhotos = diaryPhotoRepository.findAllBydiaryId(diaryId).orElse(null);

        if(diaryPhotos.size() == 0) {
            return "첨부된 사진이 없습니다.";
        } else {
            return diaryPhotos.get(0).getImgUrl();
        }
    }

}
