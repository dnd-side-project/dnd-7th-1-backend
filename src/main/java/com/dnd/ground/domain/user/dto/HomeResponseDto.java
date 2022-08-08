package com.dnd.ground.domain.user.dto;

import com.dnd.ground.domain.matrix.dto.MatrixSetDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.*;

/**
 * @description 홈화면 구성 Response Dto
 *              1. 유저 매트릭스 및 정보
 *              2. 챌린지 안하는 친구들 매트릭스
 *              3. 챌린지 하는 친구들 매트릭스 및 정보
 * @author  박세헌
 * @since   2022-08-02
 * @updated 2022-08-08 / UserResponseDto로 한번에 관리 : 박세헌
 */

@Data @Builder
public class HomeResponseDto {

    @ApiModelProperty(value="유저에 대한 정보", required = true)
    private UserResponseDto.UserMatrix userMatrices;

    @ApiModelProperty(value="(챌린지를 안하는)친구들에 대한 정보", required = true)
    private List<UserResponseDto.FriendMatrix> friendMatrices;

    @ApiModelProperty(value="(챌린지를 하는)유저들에 대한 정보",  required = true)
    private List<UserResponseDto.ChallengeMatrix> challengeMatrices;

}
