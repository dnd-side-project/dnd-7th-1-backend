package com.dnd.ground.domain.challenge.dto;

import com.dnd.ground.domain.user.dto.UserResponseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @description 챌린지 생성과 관련한 Response DTO
 * @author  박찬호
 * @since   2022-08-26
 * @updated 1. ResponseDto 생성
 *          - 2022.08.26 박찬호
 */

@Data
@Builder
public class ChallengeCreateResponseDto {
    @ApiModelProperty(value = "회원 목록", example = "\"users\": [{\"nickname\": \"NickB\"}]")
    private List<UserResponseDto.UInfo> users;

    @ApiModelProperty(value = "챌린지 메시지", example = "너~ 가보자고~")
    private String message;

    @ApiModelProperty(value = "챌린지 시작 날짜", example = "2022-08-16")
    private LocalDate started;
    
    @ApiModelProperty(value = "챌린지 종료 날짜", example = "2022-08-21")
    private LocalDate ended;
}
