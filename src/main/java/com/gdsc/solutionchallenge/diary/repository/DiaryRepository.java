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
}
