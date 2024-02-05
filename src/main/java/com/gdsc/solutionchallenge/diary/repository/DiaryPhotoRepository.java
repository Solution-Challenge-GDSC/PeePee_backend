package com.gdsc.solutionchallenge.diary.repository;

import com.gdsc.solutionchallenge.board.entity.PostPhoto;
import com.gdsc.solutionchallenge.diary.entity.DiaryPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryPhotoRepository extends JpaRepository<DiaryPhoto, Long> {
    @Query("select p from DiaryPhoto p where p.diary.diaryId = :diaryId")
    Optional<List<DiaryPhoto>> findAllBydiaryId(@Param("diaryId") Long diaryId);

    Optional<DiaryPhoto> findByFileName(String filename);
    @Query("select pp.photoId from DiaryPhoto pp where pp.diary.diaryId = :diaryId")
    List<Long> findAllId(@Param("diaryId") Long diaryId);

    @Query("select pp.imgUrl from DiaryPhoto pp where pp.diary.diaryId = :diaryId")
    List<String> findAllPhotos(@Param("diaryId") Long diaryId);

    @Modifying
    @Query("delete from DiaryPhoto pp where pp.photoId in :ids")
    void deleteAllByDiary(@Param("ids") List<Long> ids);

    @Modifying
    @Query("delete from DiaryPhoto pp where pp.diary.diaryId = :diaryId")
    void deletePostPhotoByBoardId(@Param("diaryId") Long diaryId);
}
