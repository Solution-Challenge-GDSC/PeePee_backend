package com.gdsc.solutionchallenge.user.dto;

import lombok.*;

public class UserRes {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class SignUpRes {
        private String message;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class LoginRes {
        private String message;
    }
}
