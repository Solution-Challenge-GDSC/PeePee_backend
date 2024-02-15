package com.gdsc.solutionchallenge.meetup.dto;

import com.gdsc.solutionchallenge.global.image.GetGDSRes;
import lombok.*;

import java.util.List;

@Getter
public class MeetupRes {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class GetMeetupDetailRes {
        private Long MeetupId;
        private String createDate; // ex) 2023-07-04
        private String createTime; // ex) 3분 전
        private String nickName;
        private Integer parents;
        private Integer baby;
        private String profileImage; // 작성자 프로필 이미지
        private String content;
        private List<GetGDSRes> getGDSRes; // 게시글 사진 리스트
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class GetMeetupRes {
        private Long MeetupId;
        private String createDate; // ex) 2023-07-04
        private String activityDate;
        private String content;
        private String nickName;
        private Integer parents;
        private Integer baby;
        private Double latitude;
        private Double longitude;
        //TODO: 작성자 프로필 이미지
        // private GetS3Res profile; // 작성자 프로필 이미지

    }
}