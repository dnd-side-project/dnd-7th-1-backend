package com.dnd.ground.global.exception;

import com.dnd.ground.domain.user.dto.KakaoDto;
import lombok.extern.slf4j.Slf4j;

/**
 * @description 카카오 API와 관련한 예외 클래스
 * @author 박찬호
 * @since 2023-05-19
 * @updated 1. 클래스 생성
 *          2. 카카오 문서 기반 예외를 네모두 ExceptionCodeSet으로 변환하는 메소드 생성
 *          - 2023.05.19 박찬호
 * @link <a href="https://developers.kakao.com/docs/latest/ko/reference/rest-api-reference">카카오 API 문서</a>
 */

@Slf4j
public class KakaoException extends BaseExceptionAbs {
    public KakaoException(ExceptionCodeSet exceptionCode) {
        super(exceptionCode);
    }

    public KakaoException(KakaoDto.KakaoExceptionDto dto) {
        super(changeCode(dto.getCode()));
        log.warn(">>> 카카오 예외 메시지:{}", dto.getMsg());
    }

    public KakaoException(Integer code) {
        super(changeCode(code));
    }

    public static ExceptionCodeSet changeCode(Integer code) {
        code = Math.abs(code);

        switch (code) {
            //공통 예외
            case 3:
            case 4:
            case 5:
                return ExceptionCodeSet.KAKAO_NO_AGREE;
            case 10:
            case 903:
                return ExceptionCodeSet.KAKAO_OVER_QUOTA;
            case 9798:
                return ExceptionCodeSet.KAKAO_CLOSED;
            //로그인 관련
            case 101:
            case 102:
            case 103:
            case 201:
            case 401:
            case 402:
            case 406:
                return ExceptionCodeSet.KAKAO_INVALID_TOKEN;
            default:
                return ExceptionCodeSet.KAKAO_FAILED;
        }
    }
}
