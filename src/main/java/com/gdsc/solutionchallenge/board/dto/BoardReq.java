package com.gdsc.solutionchallenge.board.dto;

import lombok.*;

public class BoardReq {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class PostBoardReq {
        private String title;
        private String content;
        private BoardType boardType;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class PatchBoardReq {
        private Long boardId;
        private String title;
        private String content;
    }


}
