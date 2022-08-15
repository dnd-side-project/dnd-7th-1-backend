package com.dnd.ground.domain.matrix.matrixService;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.user.dto.RankResponseDto;

import java.time.LocalDateTime;

/**
 * @description 운동 영역 서비스 인터페이스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 1. 챌린지 내 랭킹 결정 메소드 생성
 *          - 2022.08.15 박찬호
 */

public interface MatrixService {
    Matrix save(Matrix matrix);
    RankResponseDto.Matrix matrixRanking(String nickname, LocalDateTime start, LocalDateTime end);
    RankResponseDto.Area areaRanking(String nickname, LocalDateTime start, LocalDateTime end);
    RankResponseDto.Area challengeRank(Challenge challenge, LocalDateTime start, LocalDateTime end);
}
