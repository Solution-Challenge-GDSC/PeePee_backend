package com.gdsc.solutionchallenge.meetup.repository;

import com.gdsc.solutionchallenge.meetup.entity.Meetup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetupRepository extends JpaRepository<Meetup, Long>{
    List<Meetup> findBoardByUser_EmailOrderByMeetupIdDesc(String email);
}