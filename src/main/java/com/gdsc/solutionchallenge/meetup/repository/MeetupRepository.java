package com.gdsc.solutionchallenge.meetup.repository;

import com.gdsc.solutionchallenge.meetup.entity.Meetup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetupRepository extends JpaRepository<Meetup, Long>{
    List<Meetup> findBoardByUser_EmailOrderByMeetupIdDesc(String email);

//    @Query(value = "SELECT * FROM meetup WHERE ST_Distance_Sphere(POINT(:longitude, :latitude), geom) <= :distance", nativeQuery = true)
//    List<Meetup> findMeetupsWithinDistance(@Param("latitude") double latitude, @Param("longitude") double longitude, @Param("distance") double distance);

}