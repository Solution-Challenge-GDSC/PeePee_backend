package com.gdsc.solutionchallenge.meetup.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gdsc.solutionchallenge.global.entity.BaseTimeEntity;
import com.gdsc.solutionchallenge.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Meetup extends BaseTimeEntity {
    //meetup 주최
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meetup_id", unique = true, nullable = false)
    private Long meetupId;

    //컨텐츠 제목
    private String title;

    //활동 내용
    private String content;
    //활동 날짜
    private String activityDay;

    //활동인원(부모)
    private Integer parents;

    //활동인원(아이)
    private Integer baby;

    private Double latitude;

    private Double longitude;

    private String category;

    @Transient
    private Point point;

    // 멤버와 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // 게시 사진과 관계매핑
    @OneToMany(mappedBy = "meetup", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<MeetupPhoto> photoList = new ArrayList<>();

    public void updateMeetup(String title, String content, String activityDay, Integer parents, Integer baby, Double latitude, Double longitude, String category){
        this.title = title;
        this.content = content;
        this.activityDay = activityDay;
        this.parents = parents;
        this.baby = baby;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
    }
    public void addPhotoList(MeetupPhoto meetupPhoto){
        photoList.add(meetupPhoto);
        meetupPhoto.setMeetup(this);
    }

}