package com.dnd.ground.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @description 에러 코드 구현체
 * @author  박찬호
 * @since   2022-08-24
 * @updated 1. 에러 코드 추가 (NOT_SIGNUP_USER)
 *          -2022.09.25 박찬호
 */

@RequiredArgsConstructor
@Getter
public enum CommonErrorCode implements ErrorCode {
    //2XX~
    EXCEED_CHALLENGE(HttpStatus.ACCEPTED, "3개 이상의 챌린지에 동시에 참여 할 수 없습니다."),

    //4XX~
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "회원이 존재하지 않습니다."),
    NOT_FOUND_CHALLENGE(HttpStatus.BAD_REQUEST, "챌린지가 존재하지 않습니다."),
    NOT_FOUND_RECORD(HttpStatus.BAD_REQUEST, "운동 기록이 존재하지 않습니다."),
    NOT_FOUND_USER_CHALLENGE(HttpStatus.BAD_REQUEST, "해당 유저의 챌린지 관련 기록이 없습니다."),

    NOT_CHANGE_MASTER_STATUS(HttpStatus.BAD_REQUEST, "주최자의 상태를 변경할 수 없습니다."),

    ACCESS_DENIED(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.FORBIDDEN, "액세스 토큰이 만료 되었습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.FORBIDDEN, "리프레시 토큰이 만료 되었습니다."),
    WRONG_TOKEN(HttpStatus.FORBIDDEN, "잘못된 토큰 입니다."),
    NOT_SIGNUP_USER(HttpStatus.FORBIDDEN, "카카오 로그인만 진행하고, 회원가입은 하지 않은 유저입니다."),

    //5XX
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 동작 중 예외가 발생했습니다."),
    SQL_INTEGRITY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SQL 처리 중 무결성 문제가 발생했습니다."),
    NULL_POINTER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Null Pointer 예외가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}