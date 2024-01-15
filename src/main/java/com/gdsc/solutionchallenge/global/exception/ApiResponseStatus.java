package com.gdsc.solutionchallenge.global.exception;

import lombok.Getter;

@Getter
public enum ApiResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),

    BAD_REQUEST(false, 400, "잘못된 요청입니다"),
    FORBIDDEN(false, 400, "해당 요청에 대한 권한이 없습니다."),

    NOT_FOUND(false, 400, "일치하는 정보가 없습니다."),


    //커스텀 ERROR CODE
    USERNAME_OR_PASSWORD_NOT_FOUND_EXCEPTION(false, 400, "아이디 또는 비밀번호가 일치하지 않습니다."),
    PASSWORD_NOT_FOUND_EXCEPTION(false, 400, "비밀번호가 일치하지 않습니다."),
    NEED_TO_LOGIN(false, 400, "로그인 후 이용가능합니다."),
    EXPIRED_JWT(false, 400, "기존 토큰이 만료되었습니다. 해당 토큰을 가지고 /token/refresh 링크로 이동 후 토큰을 재발급 받으세요."),
    NEED_TO_RELOGIN(false, 400, "모든 토큰이 만료되었습니다. 다시 로그인해주세요."),

    FAILED_SIGNUP(false, 400, "회원가입에 실패하였습니다."),

    DUPLICATE_EMAIL(false, 400, "중복된 이메일입니다."),
    REGISTERED_EMAIL(false, 400, "등록된 회원입니다."),

    NOT_EXIST_POST(false, 400, "존재하지 않는 게시글입니다"),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),
    NONE_EXIST_USER(false, 2006, "존재하지 않는 사용자입니다."),
    NONE_EXIST_NICKNAME(false, 2007, "존재하지 않는 닉네임입니다."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),
    INVALID_MEMBER_ID(false, 2010, "멤버 아이디와 이메일이 일치하지 않습니다."),
    PASSWORD_CANNOT_BE_NULL(false, 2011, "비밀번호를 입력해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),
    LOG_OUT_USER(false,2019,"이미 로그아웃된 유저입니다."),
    CANNOT_UPDATE_NICKNAME(false, 2024, "동일한 닉네임으로 변경할 수 없습니다."),

    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    /**
     *   5000 : Review 관련 오류
     */
    NONE_EXIST_REVIEW(false, 5000, "요청하신 리뷰는 존재하지 않습니다."),
    INVALID_REVIEW_ID(false, 5001, "유효하지 않은 입력입니다."),
    EXCEEDED_CONTENT_LIMIT(false, 5003, "본문이 글자수 제한을 초과하였습니다."),
    USER_WITHOUT_PERMISSION(false, 5007, "본인의 리뷰에 대해서만 수정 및 삭제가 가능합니다."),

    /**
     *   6000 : 회원등록 관련 오류
     */
    DUPLICATED_NICKNAME(false, 6000, "이미 존재하는 닉네임입니다."),
    KAKAO_ERROR(false, 6001, "카카오 로그아웃에 실패했습니다."),
    ALREADY_LOGIN(false, 6004, "이미 로그인된 유저입니다."),
    ALREADY_REGISTERED_USER(false, 6005, "이미 가입된 유저입니다."),
    NICKNAME_CANNOT_BE_NULL(false, 6006, "닉네임을 입력해주세요"),
    EMAIL_CANNOT_BE_NULL(false, 6009, "이메일을 입력해주세요"),

      /**
     *   8000 : 토큰 관련 오류
     */
    EXPIRED_USER_JWT(false,8000,"만료된 JWT입니다."),
    REISSUE_TOKEN(false, 8001, "토큰이 만료되었습니다. 다시 로그인해주세요."),
    FAILED_TO_UPDATE(false, 8002, "토큰을 만료시키는 작업에 실패하였습니다."),
    FAILED_TO_REFRESH(false, 8003, "토큰 재발급에 실패하였습니다.");


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private ApiResponseStatus(boolean isSuccess, int code, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
