package com.dnd.ground.domain.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.*;

/**
 * @description 홈화면 구성 Response Dto
 * @author  박세헌, 박찬호
 * @since   2022-08-02
 * @updated 1. 회원이 진행하는 전체 챌린지 개수 필드 추가(challengeNumber)
 *          - 2022.08.09 박찬호
 */

@Data @Builder
public class HomeResponseDto {

    @ApiModelProperty(value="유저에 대한 정보")
    private UserResponseDto.UserMatrix userMatrices;

    @ApiModelProperty(value="(챌린지를 안하는)친구들에 대한 정보")
    private List<UserResponseDto.FriendMatrix> friendMatrices;

    @ApiModelProperty(value="(챌린지를 하는)유저들에 대한 정보")
    private List<UserResponseDto.ChallengeMatrix> challengeMatrices;

    @ApiModelProperty(value="회원이 진행하는 챌린지 개수")
    private Integer challengesNumber;

}
