package com.gdsc.solutionchallenge.diary.entity;

import com.gdsc.solutionchallenge.board.entity.PostPhoto;
import com.gdsc.solutionchallenge.global.entity.BaseTimeEntity;
import com.gdsc.solutionchallenge.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Diary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diaryId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int mood;

    @Column(name = "is_opened")
    private boolean isOpened; // 0이면 비공개, 1이면 공개

    // 멤버와 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "date")
    private String date;

    // 게시 사진과 관계매핑
    @OneToMany(mappedBy = "diary", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<DiaryPhoto> diaryPhotos = new ArrayList<>();


    public void updateDiary(String content, int mood, boolean isOpened){
        this.content = content;
        this.mood = mood;
        this.isOpened = isOpened;
    }

    public void addPhotoList(DiaryPhoto diaryPhoto){
        diaryPhotos.add(diaryPhoto);
        diaryPhoto.setDiary(this);
    }


}
