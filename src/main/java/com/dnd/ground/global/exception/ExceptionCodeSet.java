package com.dnd.ground.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @description 에러 코드 구현체
 * @author  박찬호
 * @since   2022-08-24
 * @updated 1.AUTH 관련 예외 코드 추가
 *          2.예외 코드 번호로 예외 코드를 반환하는 static method 이름 변경
 *          -2023.01.20 박찬호
 */

@RequiredArgsConstructor
@Getter
public enum ExceptionCodeSet {
    //회원
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "1000", "회원이 존재하지 않습니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "1001", "중복된 닉네임입니다."),

    //인증,인가
    USER_NOT_SIGNUP(HttpStatus.UNAUTHORIZED, "2000", "카카오 로그인만 진행하고, 회원가입은 하지 않은 유저입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "2001", "권한이 없습니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.FORBIDDEN, "2002", "액세스 토큰이 만료 되었습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.FORBIDDEN, "2003", "리프레시 토큰이 만료 되었습니다."),
    WRONG_TOKEN(HttpStatus.FORBIDDEN, "2004","잘못된 토큰 입니다."),
    ID_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "2005", "애플: 유효하지 않은 ID 토큰입니다."),
    ID_TOKEN_EXPIRED(HttpStatus.FORBIDDEN, "2006", "애플: ID 토큰이 만료되었습니다."),
    OAUTH_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "2007", "OAUTH 토큰이 유효하지 않습니다."),
    EMAIL_NOT_VERIFY(HttpStatus.BAD_REQUEST,"2008", "이메일이 검증되지 않았습니다."),
    LOGIN_TYPE_INVALID(HttpStatus.BAD_REQUEST, "2009", "잘못된 로그인 타입입니다."),
    SIGN_DUPLICATED(HttpStatus.BAD_REQUEST, "2010", "이미 가입된 회원입니다."),
    ISS_INVALID(HttpStatus.BAD_REQUEST, "2011", "엑세스 토큰의 iss가 올바르지 않습니다."),
    EXP_INVALID(HttpStatus.BAD_REQUEST, "2012", "엑세스 토큰의 exp가 올바르지 않습니다."),
    NBF_INVALID(HttpStatus.BAD_REQUEST, "2013", "엑세스 토큰의 nbf가 올바르지 않습니다."),
    ALGORITHM_INVALID(HttpStatus.BAD_REQUEST, "2014", "토큰의 알고리즘이 올바르지 않습니다."),
    SIGNATURE_INVALID(HttpStatus.BAD_REQUEST, "2015", "토큰의 signature가 올바르지 않습니다."),
    ACCESS_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "2016", "엑세스 토큰이 유효하지 않습니다"),
    TOKEN_EMPTY(HttpStatus.BAD_REQUEST, "2017", "토큰이 존재하지 않습니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "2018", "리프레시 토큰이 유효하지 않습니다."),

    //친구
    FRIEND_NOT_FOUND_REQ(HttpStatus.BAD_REQUEST, "3000", "친구 요청 기록이 없습니다."),
    FRIEND_NOT_FOUND(HttpStatus.BAD_REQUEST, "3001", "해당 친구가 존재하지 않습니다."),
    FRIEND_EXCEED(HttpStatus.BAD_REQUEST, "3002", "최대 친구 수를 초과하였습니다."),
    FRIEND_RES_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "3003", "친구 요청에 대한 응답을 실패했습니다."),
    FRIEND_FAIL_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, "3004", "친구 삭제를 실패했습니다."),

    //챌린지
    CHALLENGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "4000", "챌린지가 존재하지 않습니다."),
    CHALLENGE_DATE_INVALID(HttpStatus.BAD_REQUEST, "4001", "챌린지 시작 날짜는 오늘 이후부터 가능합니다."),

    //회원-챌린지
    USER_CHALLENGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "4500", "챌린지에 참가하는 회원이 아닙니다."),
    MASTER_STATUS_NOT_CHANGE(HttpStatus.BAD_REQUEST, "4501", "주최자의 상태를 변경할 수 없습니다."),
    CHALLENGE_EXCEED(HttpStatus.BAD_REQUEST, "4502", "3개 이상의 챌린지에 동시에 참여 할 수 없습니다."),

    //운동 기록 및 영역
    RECORD_NOT_FOUND(HttpStatus.BAD_REQUEST, "5000", "운동 기록이 존재하지 않습니다."),
    RANKING_CAL_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "5001", "랭킹 계산 과정에서 에러가 발생했습니다"),

    //ETC
    OK(HttpStatus.OK, "0000", "성공적으로 동작하였습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "9000", "서버 동작 중 에러가 발생했습니다."),
    MISSING_REQUIRED_PARAM(HttpStatus.BAD_REQUEST, "9001", "필수 파라미터가 없습니다."),
    NULL_POINTER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "9002", "NPE가 발생했습니다."),
    SQL_INTEGRITY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "9003", "쿼리 처리 중 에러가 발생했습니다."),
    INVALID_HTTP_METHOD(HttpStatus.BAD_REQUEST, "9004", "잘못된 HTTP Method입니다."),
    WEBCLIENT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "9005", "외부 API 통신 중 에러가 발생했습니다."),
    PARSE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "9006", "파싱 중 에러가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public static ExceptionCodeSet findExceptionByCode(String code) {
        for (ExceptionCodeSet exceptionCode : ExceptionCodeSet.values()) {
            if (exceptionCode.getCode().equals(code)) return exceptionCode;
        }
        return null;
    }
}