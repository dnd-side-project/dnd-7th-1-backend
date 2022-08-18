package com.dnd.ground.domain.matrix.controller;

import com.dnd.ground.domain.user.dto.RankResponseDto;
import com.dnd.ground.domain.user.dto.UserRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @description 메인홈 구성 컨트롤러 인터페이스
 * @author  박세헌, 박찬호
 * @since   2022-08-02
 * @updated  nickname, start, end 가진 requestDto 생성
 *          - 2022-08-18 박세헌
 */

public interface MatrixController {
    ResponseEntity<RankResponseDto.Matrix> matrixRank(@RequestParam String nickname);
    ResponseEntity<RankResponseDto.Area> areaRank(@RequestBody UserRequestDto.LookUp requestDto);
}
