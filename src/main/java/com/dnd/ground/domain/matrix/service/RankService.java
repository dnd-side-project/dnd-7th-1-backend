package com.dnd.ground.domain.matrix.service;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.exerciseRecord.dto.RankDto;
import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.RankResponseDto;
import com.dnd.ground.domain.user.dto.UserRequestDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;

import javax.persistence.Tuple;
import java.util.List;

/**
 * @description 랭킹 관련 서비스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1. 미사용 메소드 제거
 *          - 2023.03.01
 */

public interface RankService {
    Matrix save(Matrix matrix);
    RankResponseDto.Matrix matrixRankingAllTime(String nickname);
    RankResponseDto.Area areaRanking(UserRequestDto.LookUp requestDto);
    RankResponseDto.Step stepRanking(UserRequestDto.LookUp requestDto);

    List<UserResponseDto.Ranking> calculateMatrixRank(List<Tuple> matrixCount, List<User> member);
    List<UserResponseDto.Ranking> calculateAreaRank(List<UserResponseDto.Ranking> areaRankings);
    List<UserResponseDto.Ranking> calculateUsersRank(List<RankDto> rankMatrixRank);
    UserResponseDto.Ranking calculateUserRank(List<RankDto> ranks, User targetUser);
}
