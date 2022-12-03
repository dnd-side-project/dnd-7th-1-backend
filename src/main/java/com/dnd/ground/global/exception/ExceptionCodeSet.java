package com.dnd.ground.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @description 에러 코드 구현체
 * @author  박찬호
 * @since   2022-08-24
 * @updated 1.에러 코드 포맷 변경에 의한 에러 코드 재정의
 *          -2022.12.03 박찬호
 */

@RequiredArgsConstructor
@Getter
public enum ExceptionCodeSet {
    //회원
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "1000", "회원이 존재하지 않습니다."),

    //인증,인가
    USER_NOT_SIGNUP(HttpStatus.UNAUTHORIZED, "2000", "카카오 로그인만 진행하고, 회원가입은 하지 않은 유저입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "2001", "권한이 없습니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.FORBIDDEN, "2002", "액세스 토큰이 만료 되었습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.FORBIDDEN, "2003", "리프레시 토큰이 만료 되었습니다."),
    WRONG_TOKEN(HttpStatus.FORBIDDEN, "2004","잘못된 토큰 입니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "2005", "중복된 닉네임입니다."),

    //친구
    FRIEND_NOT_FOUND_REQ(HttpStatus.BAD_REQUEST, "3000", "친구 요청 기록이 없습니다."),
    FRIEND_NOT_FOUND(HttpStatus.BAD_REQUEST, "3001", "해당 친구가 존재하지 않습니다."),
    FRIEND_EXCEED(HttpStatus.BAD_REQUEST, "3002", "최대 친구 수를 초과하였습니다."),
    FRIEND_RES_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "3003", "친구 요청에 대한 응답을 실패했습니다."),
    FRIEND_FAIL_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, "3004", "친구 삭제를 실패했습니다."),

    //챌린지
    CHALLENGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "4000", "챌린지가 존재하지 않습니다."),

    //회원-챌린지
    USER_CHALLENGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "4500", "요청한 챌린지에 해당 유저의 기록이 없습니다."),
    MASTER_STATUS_NOT_CHANGE(HttpStatus.BAD_REQUEST, "4501", "주최자의 상태를 변경할 수 없습니다."),
    CHALLENGE_EXCEED(HttpStatus.BAD_REQUEST, "4502", "3개 이상의 챌린지에 동시에 참여 할 수 없습니다."),

    //운동 기록 및 영역
    RECORD_NOT_FOUND(HttpStatus.BAD_REQUEST, "5000", "운동 기록이 존재하지 않습니다."),

    //ETC
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "9000", "서버 동작 중 에러가 발생했습니다."),
    MISSING_REQUIRED_PARAM(HttpStatus.BAD_REQUEST, "9001", "필수 파라미터가 없습니다."),
    NULL_POINTER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "9002", "NPE가 발생했습니다."),
    SQL_INTEGRITY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "9003", "쿼리 처리 중 에러가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}