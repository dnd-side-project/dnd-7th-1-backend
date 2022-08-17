package com.dnd.ground.domain.matrix.matrixService;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.RankResponseDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;

import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 운동 영역 서비스 인터페이스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 1. 랭킹 계산 메소드 모듈화
 *          - 2022.08.17 박찬호
 */

public interface MatrixService {
    Matrix save(Matrix matrix);
    RankResponseDto.Matrix matrixRanking(String nickname, LocalDateTime start, LocalDateTime end);
    RankResponseDto.Area areaRanking(String nickname, LocalDateTime start, LocalDateTime end);
    RankResponseDto.Area challengeRank(Challenge challenge, LocalDateTime start, LocalDateTime end);

    List<UserResponseDto.Ranking> calculateMatrixRank(List<Tuple> matrixCount, List<User> member);
    List<UserResponseDto.Ranking> calculateAreaRank(List<UserResponseDto.Ranking> areaRankings);
}
