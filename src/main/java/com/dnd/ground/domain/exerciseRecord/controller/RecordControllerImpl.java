package com.dnd.ground.domain.exerciseRecord.controller;

import com.dnd.ground.domain.exerciseRecord.dto.EndRequestDto;
import com.dnd.ground.domain.exerciseRecord.service.ExerciseRecordService;
import com.dnd.ground.domain.user.dto.HomeResponseDto;
import com.dnd.ground.domain.user.dto.RankResponseDto;
import com.dnd.ground.domain.user.dto.UserRequestDto;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @description 기록 컨트롤러 클래스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-25 / 기록 시작 api 반환 형태 수정 - 박세헌
 */

@Api(tags = "운동기록")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/record")
@RestController
public class RecordControllerImpl implements RecordController{

    private final ExerciseRecordService exerciseRecordService;

    @GetMapping("/start")
    @Operation(summary = "기록 시작", description = "기록 시작: 운동기록 생성, 누적영역 조회")
    public ResponseEntity<HomeResponseDto> start(@RequestParam("nickname") String nickname){
        return ResponseEntity.ok(exerciseRecordService.recordStart(nickname));
    }

    @PostMapping("/end")
    @Operation(summary = "기록 끝", description = "기록 끝: 운동기록에 거리, matrix 저장")
    public ResponseEntity<?> end(@RequestBody EndRequestDto endRequestDto){
        return exerciseRecordService.recordEnd(endRequestDto);
    }

    @PostMapping("/rank/step")
    @Operation(summary = "걸음수 랭킹",
            description = "해당 유저를 기준으로 start-end(기간) 사이 걸음수가 높은 순서대로 유저와 친구들을 조회\n" +
                    "start: 해당 주 월요일 00시 00분 00초\n" +
                    "end: 해당 주 일요일 23시 59분 59초")
    public ResponseEntity<RankResponseDto.Step> stepRank(@RequestBody UserRequestDto.LookUp requestDto){
        return ResponseEntity.ok(exerciseRecordService.stepRanking(requestDto.getNickname(), requestDto.getStart(), requestDto.getEnd()));
    }
}
