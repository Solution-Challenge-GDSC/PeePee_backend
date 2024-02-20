package com.gdsc.solutionchallenge.diary.repository;

import com.gdsc.solutionchallenge.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    Optional<Diary> findByDateAndUserEmail(String date, String email);

    // 사용자가 작성한 다이어리를 제외하고 가장 최근에 작성된 5개의 공개된 다이어리를 조회하는 쿼리
    @Query(value = "SELECT * FROM diary d WHERE d.is_opened = true AND d.user_id <> (SELECT user_id FROM user WHERE email = :userEmail) ORDER BY d.created_date DESC LIMIT 5", nativeQuery = true)
    List<Diary> findTop5ByIsOpenedAndUserEmailNot(@Param("userEmail") String userEmail);

}
