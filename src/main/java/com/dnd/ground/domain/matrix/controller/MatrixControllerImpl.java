package com.dnd.ground.domain.matrix.controller;

import com.dnd.ground.domain.matrix.matrixService.MatrixService;
import com.dnd.ground.domain.user.dto.RankResponseDto;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

/**
 * @description 메인홈 구성 컨트롤러 인터페이스
 * @author  박세헌, 박찬호
 * @since   2022-08-02
 * @updated api 명세 수정 - 2022-08-17 박세헌
 */

@Api(tags = "운동 영역")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/matrix")
@RestController
public class MatrixControllerImpl implements MatrixController {

    private final MatrixService matrixService;

    @GetMapping("/rank/accumulate")
    @Operation(summary = "칸의 수 랭킹", description = "해당 유저를 기준으로 start-end(기간) 사이 칸의 수가 높은 순서대로 유저와 친구들을 조회(추후 nickname, start, end를 가진 requestDto 생성 예정)")
    public ResponseEntity<RankResponseDto.Matrix> matrixRank(@RequestParam("nickname") String nickName){

        /* 추후 nickname, start, end를 가진 requestDto 생성 예정 */
        LocalDateTime result = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime start = LocalDateTime.of(result.getYear(), result.getMonth(), result.getDayOfMonth(), 0, 0, 0);
        LocalDateTime end = LocalDateTime.now();
        /* 임시로 이번주 기록 조회 */

        return ResponseEntity.ok(matrixService.matrixRanking(nickName, start, end));
    }

    @GetMapping("/rank/widen")
    @Operation(summary = "영역의 수 랭킹", description = "해당 유저를 기준으로 start-end(기간) 사이 영역의 수가 높은 순서대로 유저와 친구들을 조회(추후 nickname, start, end를 가진 requestDto 생성 예정)")
    public ResponseEntity<RankResponseDto.Area> areaRank(@RequestParam("nickname") String nickName){

        /* 추후 nickname, start, end를 가진 requestDto 생성 예정 */
        LocalDateTime result = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime start = LocalDateTime.of(result.getYear(), result.getMonth(), result.getDayOfMonth(), 0, 0, 0);
        LocalDateTime end = LocalDateTime.now();
        /* 임시로 이번주 기록 조회 */

        return ResponseEntity.ok(matrixService.areaRanking(nickName, start, end));
    }
}
