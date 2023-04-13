package com.dnd.ground.domain.exerciseRecord.controller;

import com.dnd.ground.domain.exerciseRecord.dto.RecordCreateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @description 기록과 관련한 컨트롤러 인터페이스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1.기록 저장 API 수정(반환 타입 수정 및 객체 생성 방식 변경)
 *          2023-03-03 박찬호
 */

public interface RecordController {
    ResponseEntity<Boolean> createExerciseRecord(@RequestBody RecordCreateDto createDto);
}
