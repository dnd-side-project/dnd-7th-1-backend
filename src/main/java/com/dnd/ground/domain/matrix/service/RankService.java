package com.dnd.ground.domain.matrix.service;

import com.dnd.ground.domain.exerciseRecord.dto.RankDto;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.RankResponseDto;
import com.dnd.ground.domain.user.dto.UserRequestDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;

import java.util.List;

/**
 * @description 랭킹 관련 서비스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1. 특정 회원의 누적 랭킹 조회 추가
 *          - 2023.03.03
 */

public interface RankService {
    RankResponseDto.Matrix matrixRankingAllTime(String nickname);
    UserResponseDto.Ranking matrixUserRankingAllTime(User user);
    RankResponseDto.Area areaRanking(UserRequestDto.LookUp requestDto);
    RankResponseDto.Step stepRanking(UserRequestDto.LookUp requestDto);
    List<UserResponseDto.Ranking> calculateUsersRank(List<RankDto> rankMatrixRank);
    UserResponseDto.Ranking calculateUserRank(List<RankDto> ranks, User targetUser);
}
