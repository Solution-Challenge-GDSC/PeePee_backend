package com.gdsc.solutionchallenge.boardcomment.dto;

import com.gdsc.solutionchallenge.board.dto.BoardType;
import com.gdsc.solutionchallenge.global.image.GetGDSRes;
import lombok.*;

import java.util.List;

public class CommentReq {

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class PostCommentReq {
        private String content;
    }


    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class PatchCommentReq {
        private String content;
    }


}
