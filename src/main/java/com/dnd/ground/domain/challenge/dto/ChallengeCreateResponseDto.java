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
 * @updated 1. 챌린지 생성 API 리팩토링
 *          - 2023.02.17 박찬호
 */

@Data
@Builder
public class ChallengeCreateResponseDto {
    @ApiModelProperty(value = "회원 목록", example = "\\'users\\': [{\\'nickname\\': \\'NickB\\',\\'picturePath\\': https://dnd-ground-bucket.s3.ap-northeast-2.amazonaws.com/user/profile/default_profile.png}]")
    private List<UserResponseDto.UInfo> members;

    @ApiModelProperty(value = "챌린지 메시지", example = "너~ 가보자고~")
    private String message;

    @ApiModelProperty(value = "챌린지 시작 날짜", example = "2022-08-16")
    private LocalDate started;
    
    @ApiModelProperty(value = "챌린지 종료 날짜", example = "2022-08-21")
    private LocalDate ended;

    @ApiModelProperty(value = "챌린지에서 제외된 멤버 수", example = "2")
    private Integer exceptMemberCount;

    @ApiModelProperty(value = "챌린지에서 제외된 멤버 닉네임", example = "[NickB, NickC]")
    private List<String> exceptMembers;
}
