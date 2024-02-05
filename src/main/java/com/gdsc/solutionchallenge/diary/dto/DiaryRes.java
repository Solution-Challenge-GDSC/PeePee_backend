package com.gdsc.solutionchallenge.diary.dto;

import com.gdsc.solutionchallenge.board.dto.BoardType;
import com.gdsc.solutionchallenge.global.image.GetGDSRes;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class DiaryRes {

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class GetDiaryRes {
        private Long diaryId;
        private String content;
        private int mood;
        private boolean isOpened; // 0이면 비공개, 1이면 공개
        private String date;
        private List<GetGDSRes> getDiaryPost; // 다이어리 사진 리스트
    }


}
