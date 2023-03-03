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
 * @updated 1.영역 조회를 유연하게 하기 위한 파라미터 변경
 *          - 2023-03-03 박찬호
 */
public interface MatrixRepositoryQuery {
    List<Location> findMatrixPoint(MatrixCond condition);
    Map<User, List<Location>> findUsersMatrix(Set<User> users, Location location, double spanDelta);
}
