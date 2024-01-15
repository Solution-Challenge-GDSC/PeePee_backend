package com.gdsc.solutionchallenge.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Component
@RequestMapping
public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String exception = (String) request.getAttribute("exception");
        ApiResponseStatus errorCode;

        if(exception == null) {
            errorCode = ApiResponseStatus.NEED_TO_LOGIN;
            setResponse(response, errorCode);
            return;
        }

        if(exception.equals("NullPointerException")) {
            errorCode = ApiResponseStatus.NEED_TO_LOGIN;
            setResponse(response, errorCode);
            return;
        }


        if(exception.equals("PasswordNotFoundException")) {
            errorCode = ApiResponseStatus.PASSWORD_NOT_FOUND_EXCEPTION;
            setResponse(response, errorCode);
            return;
        }

        if(exception.equals("ForbiddenException")) {
            errorCode = ApiResponseStatus.FORBIDDEN;
            setResponse(response, errorCode);
            return;
        }

        //토큰이 만료된 경우
        if(exception.equals("ExpiredJwtException")) {
            errorCode = ApiResponseStatus.EXPIRED_JWT;
            setResponse(response, errorCode);
            return;
        }

        //아이디 비밀번호가 다를 경우
        if(exception.equals("UsernameOrPasswordNotFoundException")) {
            errorCode = ApiResponseStatus.USERNAME_OR_PASSWORD_NOT_FOUND_EXCEPTION;
            setResponse(response, errorCode);
            return;
        }

    }

    private void setResponse(HttpServletResponse response, ApiResponseStatus errorCode) throws IOException {
        JSONObject json = new JSONObject();
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        json.put("code", errorCode.getCode());
        json.put("message", errorCode.getMessage());
        response.getWriter().print(json);

    }
}
