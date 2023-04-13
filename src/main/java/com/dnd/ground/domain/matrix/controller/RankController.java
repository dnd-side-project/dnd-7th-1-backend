package com.dnd.ground.domain.matrix.controller;

import com.dnd.ground.domain.user.dto.RankResponseDto;
import com.dnd.ground.domain.user.dto.UserRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @description 랭킹 관련 컨트롤러 인터페이스
 * @author  박찬호
 * @since   2022-08-02
 * @updated  1.클래스 이름 변경
 *          - 2023-03-10 박찬호
 */

public interface RankController {
    ResponseEntity<RankResponseDto.Matrix> matrixRank(@RequestParam String nickname);
    ResponseEntity<RankResponseDto.Area> areaRank(@RequestBody UserRequestDto.LookUp requestDto);
    ResponseEntity<RankResponseDto.Step> stepRank(@ModelAttribute UserRequestDto.LookUp requestDto);
}
