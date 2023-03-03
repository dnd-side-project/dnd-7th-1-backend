package com.dnd.ground.domain.challenge.dto;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @description 챌린지와 관련한 Request DTO
 * @author  박찬호
 * @since   2022-08-08
 * @updated 1. 유효성 검증을 위해 @NotNull -> @NotBlank 수정
 *          - 2023.02.18 박찬호
 */


@Data
public class ChallengeRequestDto {

    //유저-챌린지 정보를 위한 이너 클래스
    @Data
    static public class CInfo {
        @NotBlank(message = "UUID가 필요합니다.")
        @ApiParam(value="UUID", example="11ed1e42ae1af37a895b2f2416025f66", required = true, type="path")
        private String uuid;

        @NotBlank(message = "닉네임이 필요합니다.")
        @ApiParam(value="회원 닉네임", example="NickA", required = true, type="path")
        private String nickname;
    }
}
