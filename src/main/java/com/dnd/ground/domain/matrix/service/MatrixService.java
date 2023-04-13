package com.dnd.ground.domain.matrix.service;

import com.dnd.ground.domain.matrix.dto.MatrixRequestDto;
import com.dnd.ground.domain.matrix.dto.MatrixResponseDto;

import java.util.List;
/**
 * @description 영역 조회와 관련한 Service 인터페이스
 * @author  박찬호
 * @since   2023.03.12
 * @updated 1. 특정 영역 검색 API 생성
 *          - 2023.03.12 박찬호
 */
public interface MatrixService {
    List<MatrixResponseDto> getMatrix(MatrixRequestDto request);
}
