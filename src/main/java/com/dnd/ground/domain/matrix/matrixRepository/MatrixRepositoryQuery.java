package com.dnd.ground.domain.matrix.matrixRepository;

import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.matrix.dto.Location;

import java.util.List;

/**
 * @description 운동 기록(영역) 관련 QueryDSL 레포지토리와 Data JPA를 같이 사용하기 위한 인터페이스
 * @author  박찬호
 * @since   2023-02-14
 * @updated 1.일정 거리 내 영역 조회 메소드 생성
 *          - 2023-02-14 박찬호
 */
public interface MatrixRepositoryQuery {
    List<Location> findMatrixPoint(User user, Location location);
}
