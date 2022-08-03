package com.dnd.ground.domain.challenge.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @description 챌린지와 관련한 Request DTO
 * @author  박찬호
 * @since   2022-08-03
 * @updated 1. 챌린지 생성을 위한 Request DTO 생성
 *          - 2022.08.03 박찬호
 */

@Data
@AllArgsConstructor
public class ChallengeCreateRequestDto {

    @NotNull(message = "챌린지 이름이 필요합니다.")
    @ApiModelProperty(value="챌린지 이름", example="챌린지1", required = true)
    private String name;

    @ApiModelProperty(value="신청 메시지", example="챌린지 신청 메시지")
    private String message;

    @NotNull(message = "챌린지 색상이 필요합니다.")
    @ApiModelProperty(value="챌린지 색상", example="#FFFFFF", required = true)
    private String color;

    @NotNull(message = "함께하는 친구가 1명 이상이어야 합니다.")
    @ApiModelProperty(value="함께하는 닉네임 리스트", example="[nick1, nick2 ...]", required = true)
    private Set<String> nicknames;

}
