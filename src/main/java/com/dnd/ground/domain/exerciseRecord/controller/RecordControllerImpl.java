package com.dnd.ground.domain.exerciseRecord.controller;

import com.dnd.ground.domain.exerciseRecord.dto.EndRequestDto;
import com.dnd.ground.domain.exerciseRecord.dto.StartResponseDto;
import com.dnd.ground.domain.exerciseRecord.service.ExerciseRecordService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @description 기록 컨트롤러 클래스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-04 / record Api 구현: 박세헌
 */

@Api(tags = "운동기록")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/record")
@RestController
public class RecordControllerImpl implements RecordController{

    private final ExerciseRecordService exerciseRecordService;

    @PostMapping("/start")
    public ResponseEntity<StartResponseDto> start(@RequestParam("nickname") String nickname){
        return ResponseEntity.ok(exerciseRecordService.recordStart(nickname));
    }

    @PostMapping("/end")
    public ResponseEntity<?> end(@RequestBody EndRequestDto endRequestDto){
        return exerciseRecordService.recordEnd(endRequestDto);
    }
}
