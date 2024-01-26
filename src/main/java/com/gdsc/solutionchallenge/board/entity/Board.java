package com.gdsc.solutionchallenge.board.entity;

import com.gdsc.solutionchallenge.board.dto.BoardType;
import com.gdsc.solutionchallenge.global.entity.BaseTimeEntity;
import com.gdsc.solutionchallenge.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Board extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @Column(columnDefinition = "bigint default 0")
    private Long commentCount; // 댓글 수

    @Column(columnDefinition = "bigint default 0")
    private Long likeCount; // 좋아요 수

    // 멤버와 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 게시 사진과 관계매핑
    @OneToMany(mappedBy = "board", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<PostPhoto> photoList = new ArrayList<>();


    public void updateBoard(String title, String content){
        this.title = title;
        this.content = content;
    }

    public void updateBoardType(BoardType boardType){
        this.boardType = boardType;
    }

    public void addPhotoList(PostPhoto postPhoto){
        photoList.add(postPhoto);
        postPhoto.setBoard(this);
    }

}
