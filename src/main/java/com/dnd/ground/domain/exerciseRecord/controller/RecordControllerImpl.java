package com.dnd.ground.domain.exerciseRecord.controller;

import com.dnd.ground.domain.exerciseRecord.dto.RecordCreateDto;
import com.dnd.ground.domain.exerciseRecord.service.ExerciseRecordService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @description 기록과 관련한 컨트롤러
 * @author   박찬호
 * @since   2022-08-01
 * @updated 1.기록 저장 API 수정(반환 타입 수정 및 객체 생성 방식 변경)
 *          2.메소드 이름 변경 (end -> createExerciseRecord)
 *          2023-03-05 박찬호
 */

@Api(tags = "운동기록")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/record")
@RestController
public class RecordControllerImpl implements RecordController {

    private final ExerciseRecordService exerciseRecordService;

    @PostMapping("/end")
    @Operation(summary = "기록 끝", description = "기록 끝: 운동기록에 거리, matrix 저장")
    public ResponseEntity<Boolean> createExerciseRecord(@RequestBody RecordCreateDto createDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(exerciseRecordService.createExerciseRecord(createDto));
    }
}
