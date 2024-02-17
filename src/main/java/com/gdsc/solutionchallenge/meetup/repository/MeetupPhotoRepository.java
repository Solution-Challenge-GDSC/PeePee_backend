package com.gdsc.solutionchallenge.meetup.repository;

import com.gdsc.solutionchallenge.meetup.entity.MeetupPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MeetupPhotoRepository extends JpaRepository<MeetupPhoto, Long> {
    @Query("select p from MeetupPhoto p where p.meetup.meetupId = :meetupId")
    Optional<List<MeetupPhoto>> findAllByMeetupId(@Param("meetupId") Long meetupId);

    Optional<MeetupPhoto> findByFileName(String filename);

    @Query("select pp.photoId from MeetupPhoto pp where pp.meetup.meetupId = :meetupId")
    List<Long> findAllId(@Param("meetupId") Long meetupId);

    @Query("select pp.imgUrl from MeetupPhoto pp where pp.meetup.meetupId = :meetupId")
    List<String> findAllPhotos(@Param("meetupId") Long meetupId);

    @Modifying
    @Query("delete from MeetupPhoto pp where pp.photoId in :ids")
    void deleteAllByMeetup(@Param("ids") List<Long> ids);

    @Modifying
    @Query("delete from MeetupPhoto pp where pp.meetup.meetupId = :meetupId")
    void deleteMeetupPhotoByMeetupId(@Param("meetupId") Long meetupId);
}