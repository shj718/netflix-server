package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),
    POST_USERS_EMPTY_PASSWORD(false, 2018, "비밀번호를 입력해주세요."),
    POST_USERS_EMPTY_MEMBERSHIP(false,2019,"멤버십을 선택해주세요."),
    POST_USERS_INVALID_MEMBERSHIP(false,2020,"멤버십의 형식을 확인해주세요."),
    PAYMENT_EMPTY_CARD_NUMBER(false,2021,"카드 번호를 입력해주세요."),
    PAYMENT_EMPTY_NAME(false,2022,"이름을 입력해주세요."),
    INVALID_PROFILE_LOCK_REQUEST(false, 2023, "프로필 잠금을 해제하려면 N을, PIN 번호를 설정하려면 4자리 숫자를 입력해주세요."),
    EMPTY_PROFILE_LOCK_PIN(false, 2024, "프로필 잠금 PIN 번호를 입력해주세요."),
    PATCH_EMPTY_PROFILE_LOCK(false,2025,"프로필 잠금 정보를 입력해주세요."),
    INVALID_PROFILE_LOCK_PIN(false,2026,"PIN 번호의 형식을 확인해주세요."),


    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"비밀번호가 틀렸습니다."),
    EMAIL_NOT_EXISTS(false,3015,"가입되지 않은 이메일입니다."),
    EXCEEDED_PROFILES_COUNT(false, 3016, "만들 수 있는 프로필은 최대 5개입니다."),
    PROFILE_LOGIN_NOT_LOCKED(false,3017,"잠금 설정이 해제된 프로필입니다."),
    PROFILE_NOT_EXISTS(false,3018,"존재하지 않는 프로필 고유 번호입니다."),
    FAILED_TO_PROFILE_LOGIN(false,3019,"PIN 번호가 틀렸습니다."),



    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다.");


    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
