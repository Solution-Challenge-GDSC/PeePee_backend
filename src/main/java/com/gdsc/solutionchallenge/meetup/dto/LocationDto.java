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
        private double longitude;

        @NotNull
        private double latitude;

        public static Post of(double longitude, double latitude) {
            return Post.builder()
                    .longitude(longitude)
                    .latitude(latitude)
                    .build();
        }
    }
    @Getter
    @ToString
    @Builder
    public static class Patch {

        private double longitude;

        private double latitude;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private double longitude;

        private double latitude;
    }
}
