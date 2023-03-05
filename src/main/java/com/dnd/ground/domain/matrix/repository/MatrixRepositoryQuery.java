package com.dnd.ground.domain.matrix.repository;

import com.dnd.ground.domain.matrix.dto.MatrixCond;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.matrix.dto.Location;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @description 운동 기록(영역) 관련 QueryDSL 레포지토리와 Data JPA를 같이 사용하기 위한 인터페이스
 * @author  박찬호
 * @since   2023-02-14
 * @updated 1.중복을 제외한 영역 조회 쿼리 생성
 *          2.중복을 제외한 영역 개수 쿼리 생성
 *          - 2023-03-05 박찬호
 */
public interface MatrixRepositoryQuery {
    List<Location> findMatrixPoint(MatrixCond condition);
    List<Location> findMatrixPointDistinct(MatrixCond condition);
    Map<User, List<Location>> findUsersMatrix(Set<User> users, Location location, double spanDelta);
    long matrixCount(MatrixCond condition);
    long matrixCountDistinct(MatrixCond condition);
}
