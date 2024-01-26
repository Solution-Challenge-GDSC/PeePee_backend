package com.gdsc.solutionchallenge.board.dto;

import com.gdsc.solutionchallenge.global.image.GetGDSRes;
import lombok.*;

import java.util.List;

public class BoardRes {



    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class GetBoardDetailRes {
        private Long boardId;
        private BoardType boardType;
        private String createDate; // ex) 2023-07-04
        private String createTime; // ex) 3분 전
        private String nickName;
        private String profileImage; // 작성자 프로필 이미지
        private String title;
        private String content;
        private Long commentCount; // 댓글 수
        private Long likeCount; // 좋아요 수
       private List<GetGDSRes> getGDSRes; // 게시글 사진 리스트
       //TODO: 댓글 리스트
       //private List<GetCommentRes> getCommentRes;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class GetBoardRes {
        private Long boardId;
        private BoardType boardType;
        private String createDate; // ex) 2023-07-04
        private String createTime; // ex) 3분 전
        private String nickName;
        //TODO: 작성자 프로필 이미지
        // private GetS3Res profile; // 작성자 프로필 이미지
        private String title;
        private String content;
        private Long commentCount; // 댓글 수
        private Long likeCount; // 좋아요 수
        //TODO: 게시글 사진 리스트
        //private List<GetS3Res> getS3Res;
        //TODO: 댓글 리스트
        //private List<GetCommentRes> getCommentRes;
    }



}
