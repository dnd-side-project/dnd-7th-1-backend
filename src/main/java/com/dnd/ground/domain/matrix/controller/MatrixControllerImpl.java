package com.dnd.ground.domain.matrix.controller;

import com.dnd.ground.domain.matrix.matrixService.MatrixService;
import com.dnd.ground.domain.user.dto.RankResponseDto;
import com.dnd.ground.domain.user.dto.UserRequestDto;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * @description 메인홈 구성 컨트롤러 인터페이스
 * @author  박세헌, 박찬호
 * @since   2022-08-02
 * @updated 1. 역대 누적 칸수 랭킹 구현
 *          2. nickname, start, end 가진 requestDto 생성
 *          - 2022-08-18 박세헌
 */

@Api(tags = "운동 영역")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/matrix")
@RestController
public class MatrixControllerImpl implements MatrixController {

    private final MatrixService matrixService;

    @GetMapping("/rank/accumulate")
    @Operation(summary = "역대 누적 칸의 수 랭킹", description = "해당 유저를 기준으로 가입날짜 ~ 오늘 사이 누적 칸의 수가 높은 순서대로 유저와 친구들을 조회")
    public ResponseEntity<RankResponseDto.Matrix> matrixRank(@RequestParam("nickname") String nickname){
        return ResponseEntity.ok(matrixService.matrixRanking(nickname));
    }

    @PostMapping("/rank/widen")
    @Operation(summary = "영역의 수 랭킹",
            description = "해당 유저를 기준으로 start-end(기간) 사이 영역의 수가 높은 순서대로 유저와 친구들을 조회\n" +
            "start: 해당 주 월요일 00시 00분 00초\n" +
            "end: 해당 주 일요일 23시 59분 59초")
    public ResponseEntity<RankResponseDto.Area> areaRank(@RequestBody UserRequestDto.LookUp requestDto){
        return ResponseEntity.ok(matrixService.areaRanking(requestDto.getNickname(), requestDto.getStart(), requestDto.getEnd()));
    }
}
