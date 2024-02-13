package com.gdsc.solutionchallenge.meetup.dto;

import lombok.*;

import java.util.Date;

public class MeetupReq {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class PostMeetupReq {
        private String content;
        private String activityDay;
        private Integer parents;
        private Integer baby;
        private Double latitude;
        private Double longitude;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class PatchMeetupReq {
        private Long MeetupId;
        private String content;
        private String activityDay;
        private Integer parents;
        private Integer baby;
        private Double latitude;
        private Double longitude;
    }
}
