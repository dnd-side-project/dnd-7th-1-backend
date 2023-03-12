package com.dnd.ground.domain.matrix.repository;

import com.dnd.ground.domain.matrix.dto.MatrixCond;
import com.dnd.ground.domain.matrix.dto.MatrixResponseDto;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.matrix.dto.Location;

import java.util.List;
import java.util.Map;

/**
 * @description 운동 기록(영역) 관련 QueryDSL 레포지토리와 Data JPA를 같이 사용하기 위한 인터페이스
 * @author  박찬호
 * @since   2023-02-14
 * @updated 1.특정 인원의 영역 조회 쿼리 생성
 *          2.챌린지 기간동안 생성한 영역 조회 쿼리 생성
 *          - 2023-03-12 박찬호
 */
public interface MatrixRepositoryQuery {
    List<Location> findMatrixList(MatrixCond condition);
    List<Location> findMatrixListDistinct(MatrixCond condition);
    Map<User, List<Location>> findMatrixMap(MatrixCond condition);
    Map<User, List<Location>> findMatrixMapDistinct(MatrixCond condition);
    long matrixCount(MatrixCond condition);
    long matrixCountDistinct(MatrixCond condition);
    List<MatrixResponseDto> findMatrix(MatrixCond condition);
    List<MatrixResponseDto> findChallengeMatrix(MatrixCond condition);
}
