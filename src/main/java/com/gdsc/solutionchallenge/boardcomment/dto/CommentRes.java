package com.gdsc.solutionchallenge.boardcomment.dto;

import com.gdsc.solutionchallenge.board.dto.BoardType;
import com.gdsc.solutionchallenge.global.image.GetGDSRes;
import lombok.*;

import java.util.List;

public class CommentRes {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class PostCommentRes {
        private Long commentId;    // 댓글 고유 PK
        private String content;    // 댓글 내용
        private String nickName;   // 회원 닉네임
        private String profile; // 회원 프로필 사진
        private String createdDate;
        private String modifiedDate;
    }


    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class GetCommentRes {
        private Long commentId;    // 댓글 고유 PK
        private String content;    // 댓글 내용
        private String nickName;   // 회원 닉네임
        private String profile; // 회원 프로필 사진
        private String createdDate;
        private String modifiedDate;
    }


    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class PatchCommentRes {
        private Long commentId;    // 댓글 고유 PK
        private String content;    // 댓글 내용
        private String nickName;   // 회원 닉네임
        private String modifiedDate;
    }

}
