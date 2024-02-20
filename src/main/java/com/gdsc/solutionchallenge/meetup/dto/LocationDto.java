package com.gdsc.solutionchallenge.meetup.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

public class LocationDto {
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    @Builder
    public static class Post {
        @NotNull
        private double latitude;

        @NotNull
        private double longitude;

        public static Post of(double latitude, double longitude) {
            return Post.builder()
                    .latitude(latitude)
                    .longitude(longitude)
                    .build();
        }
    }
    @Getter
    @ToString
    @Builder
    public static class Patch {
        private double latitude;

        private double longitude;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private double latitude;

        private double longitude;
    }
}