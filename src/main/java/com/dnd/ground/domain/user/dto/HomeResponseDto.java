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
 * @updated 1. 메인화면 필터와 관련된 필드 추가
 *          - 2022.08.08 박찬호
 */

@Data @Builder
public class HomeResponseDto {

    @ApiModelProperty(value="유저에 대한 정보")
    private UserResponseDto.UserMatrix userMatrices;

    @ApiModelProperty(value="(챌린지를 안하는)친구들에 대한 정보")
    private List<UserResponseDto.FriendMatrix> friendMatrices;

    @ApiModelProperty(value="(챌린지를 하는)유저들에 대한 정보")
    private List<UserResponseDto.ChallengeMatrix> challengeMatrices;

    @ApiModelProperty(value="회원이 진행하는 챌린지 개수", example="5")
    private Integer challengesNumber;

    @ApiModelProperty(value="메인화면 필터: 나의 기록 보기", example="true")
    private Boolean isShowMine;

    @ApiModelProperty(value="메인화면 필터: 친구 보기", example="false")
    private Boolean isShowFriend;

    @ApiModelProperty(value="메인화면 필터: 친구들에게 보이기", example="true")
    private Boolean isPublicRecord;
}
