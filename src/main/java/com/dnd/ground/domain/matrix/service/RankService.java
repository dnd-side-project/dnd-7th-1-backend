package com.dnd.ground.domain.matrix.service;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.RankResponseDto;
import com.dnd.ground.domain.user.dto.UserRequestDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;

import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 랭킹 관련 서비스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1.걸음수 랭킹 API 리팩토링 및 위치 변경
 *          2023-02-22 박찬호
 */

public interface RankService {
    Matrix save(Matrix matrix);
    RankResponseDto.Matrix matrixRankingAllTime(String nickname);
    RankResponseDto.Area areaRanking(UserRequestDto.LookUp requestDto);
    RankResponseDto.Step stepRanking(UserRequestDto.LookUp requestDto);
    RankResponseDto.Area challengeRank(Challenge challenge, LocalDateTime start, LocalDateTime end);

    List<UserResponseDto.Ranking> calculateMatrixRank(List<Tuple> matrixCount, List<User> member);
    List<UserResponseDto.Ranking> calculateAreaRank(List<UserResponseDto.Ranking> areaRankings);
}
