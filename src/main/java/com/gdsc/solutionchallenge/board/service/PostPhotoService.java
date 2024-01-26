package com.gdsc.solutionchallenge.board.service;

import com.gdsc.solutionchallenge.board.entity.Board;
import com.gdsc.solutionchallenge.board.entity.PostPhoto;
import com.gdsc.solutionchallenge.board.repository.PostPhotoRepository;
import com.gdsc.solutionchallenge.global.image.GetGDSRes;
import com.gdsc.solutionchallenge.global.image.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostPhotoService {
    private final PostPhotoRepository postPhotoRepository;
    private final ImageUploadService imageUploadService;

    @Transactional
    public void savePostPhoto(List<PostPhoto> postPhotos){
        postPhotoRepository.saveAll(postPhotos);
    }

    /**
     *  여러 개의 PostPhoto 저장
     */
    @Transactional
    public void saveAllPostPhotoByBoard(List<GetGDSRes> getGDSResList , Board board) {
        // PostPhoto 리스트를 받아옴
        List<PostPhoto> postPhotos = new ArrayList<>();
        for (GetGDSRes getGDSRes : getGDSResList) {
            PostPhoto newPostPhoto = PostPhoto.builder()
                    .imgUrl(getGDSRes.getImgUrl())
                    .fileName(getGDSRes.getFileName())
                    .build();
            postPhotos.add(newPostPhoto);
            board.addPhotoList(newPostPhoto);
        }
        savePostPhoto(postPhotos);
    }

    /**
     * 게시글과 연관된 모든 postPhoto 삭제
     */
    @Transactional
    public void deleteAllPostPhotoByBoard(List<Long> ids){
        postPhotoRepository.deleteAllByBoard(ids);
    }

    @Transactional
    public void deleteAllPostPhotos(List<PostPhoto> postPhotos){
        for (PostPhoto postPhoto : postPhotos) {
            imageUploadService.deleteImage(postPhoto.getFileName());
        }
    }

    /**
     * 게시글과 연관된 모든 postPhoto 의 imgUrl 조회
     */
    public List<String> findAllPhotosByPostId(Long boardId){
        return postPhotoRepository.findAllPhotos(boardId);
    }

    /**
     * 게시글와 연관된 모든 id 조회
     */
    public List<Long> findAllId(Long boardId){
        return postPhotoRepository.findAllId(boardId);
    }

    public List<PostPhoto> findAllByBoardId(Long boardId){
        return postPhotoRepository.findAllByBoardId(boardId).orElse(null);
    }

    /**
     * 썸네일 적용을 위한 메서드
     */
    public String findFirstByPostId(Long boardId) {
        List<PostPhoto> postPhotos = postPhotoRepository.findAllByBoardId(boardId).orElse(null);

        if(postPhotos.size() == 0) {
            return "첨부된 사진이 없습니다.";
        } else {
            return postPhotos.get(0).getImgUrl();
        }
    }

}
