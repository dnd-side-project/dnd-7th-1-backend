package com.dnd.ground.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;

import java.util.List;

/**
 * @description 예외 응답 메시지 포맷 클래스
 * @author  박찬호
 * @since   2022-08-24
 * @updated 1. 상황에 따른 nickname 필드 추가
 *          - 2022.08.25 박찬호
 */

@Getter
@Builder
@RequiredArgsConstructor
public class ErrorResponse {

    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final String nickname;

    @JsonInclude(JsonInclude.Include.NON_EMPTY) //값이 없으면 JSON 포함X
    private final List<ValidationError> errors;

    /*@Valid를 활용해 유효성 검증 시 발생하는 예외처리*/
    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class ValidationError {

        private final String field;
        private final String message;

        public static ValidationError of(final FieldError fieldError) {
            return ValidationError.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build();
        }
    }
}