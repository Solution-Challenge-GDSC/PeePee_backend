package com.gdsc.solutionchallenge.diary.dto;

import com.gdsc.solutionchallenge.board.dto.BoardType;
import com.gdsc.solutionchallenge.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;

public class DiaryReq {



    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class postDiaryReq {
        private String content;
        private int mood;
        private boolean isOpened; // 0이면 비공개, 1이면 공개
        private String date;
    }



    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class patchDiaryReq {
        private Long diaryId;
        private String content;
        private int mood;
        private boolean isOpened; // 0이면 비공개, 1이면 공개
    }

}
