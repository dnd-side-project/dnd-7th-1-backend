package com.dnd.ground.domain.exerciseRecord.controller;

import com.dnd.ground.domain.exerciseRecord.dto.EndRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @description 기록 컨트롤러 인터페이스
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated 1.걸음수 랭킹 API 리팩토링 및 위치 변경
 *          2023-02-22 박찬호
 */

public interface RecordController {
    ResponseEntity<?> end(@RequestBody EndRequestDto endRequestDto);
}
