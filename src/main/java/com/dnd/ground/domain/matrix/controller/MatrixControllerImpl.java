package com.dnd.ground.domain.matrix.controller;

import com.dnd.ground.domain.matrix.dto.MatrixRequestDto;
import com.dnd.ground.domain.matrix.dto.MatrixResponseDto;
import com.dnd.ground.domain.matrix.service.MatrixService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description 영역 관련 컨트롤러 인터페이스
 * @author  박찬호
 * @since   2023-03-08
 * @updated  1.랭킹과 영역 관련 역할 분리
 *           2.영역 조회 API 생성
 *          - 2023-03-12 박찬호
 */

@RestController
@Api(tags = "영역")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/matrix")
public class MatrixControllerImpl {
    private final MatrixService matrixService;

    @GetMapping("")
    public List<MatrixResponseDto> getMatrix(@ModelAttribute MatrixRequestDto request) {
        return matrixService.getMatrix(request);
    }

}
