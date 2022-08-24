package com.dnd.ground.global.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description JWT 유효성 검사 결과 클래스(토큰 subject, 유효성 여부)
 * @author  박세헌
 * @since   2022-08-24
 * @updated 1. 생성
 *          - 2022.08.24 박세헌
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtVerifyResult {
    private boolean success;
    private String nickname;
}
