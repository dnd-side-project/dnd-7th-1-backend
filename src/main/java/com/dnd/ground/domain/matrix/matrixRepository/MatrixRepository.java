package com.dnd.ground.domain.matrix.matrixRepository;

import com.dnd.ground.domain.matrix.Matrix;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @description 운동 영역 리포지토리 인터페이스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-01 / 생성 : 박세헌
 */

public interface MatrixRepository extends JpaRepository<Matrix, Long> {
}
