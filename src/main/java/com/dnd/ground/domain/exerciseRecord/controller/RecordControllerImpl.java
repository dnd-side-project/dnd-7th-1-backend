package com.dnd.ground.domain.exerciseRecord.controller;

import com.dnd.ground.domain.exerciseRecord.dto.EndRequestDto;
import com.dnd.ground.domain.exerciseRecord.dto.StartResponseDto;
import com.dnd.ground.domain.exerciseRecord.service.ExerciseRecordService;
import com.dnd.ground.domain.user.dto.RankResponseDto;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

/**
 * @description 기록 컨트롤러 클래스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-16 / 기록 중지 api 삭제 - 박세헌
 */

@Api(tags = "운동기록")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/record")
@RestController
public class RecordControllerImpl implements RecordController{

    private final ExerciseRecordService exerciseRecordService;

    @PostMapping("/start")
    @Operation(summary = "기록 시작", description = "기록 시작: 운동기록 생성, 누적영역 조회")
    public ResponseEntity<StartResponseDto> start(@RequestParam("nickname") String nickname){
        return ResponseEntity.ok(exerciseRecordService.recordStart(nickname));
    }

    @PostMapping("/end")
    @Operation(summary = "기록 끝", description = "기록 끝: 운동기록에 거리, matrix 저장")
    public ResponseEntity<?> end(@RequestBody EndRequestDto endRequestDto){
        return exerciseRecordService.recordEnd(endRequestDto);
    }

    @GetMapping("/rank/step")
    @Operation(summary = "걸음수 랭킹", description = "걸음수가 높은 순서대로 유저들을 조회")
    public ResponseEntity<RankResponseDto.Step> matrixRank(@RequestParam("nickname") String nickName){

        /* 추후 nickname, start, end를 가진 requestDto 생성 예정 */
        LocalDateTime result = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime start = LocalDateTime.of(result.getYear(), result.getMonth(), result.getDayOfMonth(), 0, 0, 0);
        LocalDateTime end = LocalDateTime.now();
        /* 임시로 이번주 기록 */

        return ResponseEntity.ok(exerciseRecordService.stepRanking(nickName, start, end));
    }
}
