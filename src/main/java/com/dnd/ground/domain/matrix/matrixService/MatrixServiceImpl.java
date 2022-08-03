package com.dnd.ground.domain.matrix.matrixService;

import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.matrix.matrixRepository.MatrixRepository;
import lombok.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description 운동 영역 서비스 클래스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-01 / 생성 : 박세헌
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatrixServiceImpl implements MatrixService{
    private final MatrixRepository matrixRepository;

    @Transactional
    public Matrix save(Matrix matrix){
        return matrixRepository.save(matrix);
    }
}
