package com.gdsc.solutionchallenge.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiException extends RuntimeException {
    private ApiResponseStatus status; // ApiResponseStatus 객체에 매핑
}
