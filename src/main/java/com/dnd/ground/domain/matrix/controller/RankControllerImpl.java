package com.dnd.ground.domain.matrix.controller;

import com.dnd.ground.domain.matrix.service.RankService;
import com.dnd.ground.domain.user.dto.RankResponseDto;
import com.dnd.ground.domain.user.dto.UserRequestDto;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * @description 랭킹 관련 컨트롤러 구현체
 * @author  박찬호
 * @since   2022-08-02
 * @updated  1.클래스 이름 변경
 *          - 2023-03-10 박찬호
 */

@Api(tags = "랭킹")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/matrix/rank")
@RestController
public class RankControllerImpl implements RankController {

    private final RankService rankService;

    @GetMapping("/accumulate")
    @Operation(summary = "역대 누적 칸의 수 랭킹", description = "해당 유저를 기준으로 가입날짜 ~ 오늘 사이 누적 칸의 수가 높은 순서대로 유저와 친구들을 조회")
    public ResponseEntity<RankResponseDto.Matrix> matrixRank(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok(rankService.matrixRankingAllTime(nickname));
    }

    @GetMapping("/widen")
    @Operation(summary = "영역의 수 랭킹",
            description = "해당 유저를 기준으로 start-end(기간) 사이 영역의 수가 높은 순서대로 유저와 친구들을 조회\n" +
            "started: 해당 주 월요일 00시 00분 00초\n" +
            "ended: 해당 주 일요일 23시 59분 59초")
    public ResponseEntity<RankResponseDto.Area> areaRank(@ModelAttribute UserRequestDto.LookUp requestDto) {
        return ResponseEntity.ok(rankService.areaRanking(requestDto));
    }

    @GetMapping("/step")
    @Operation(summary = "걸음수 랭킹",
            description = "해당 유저를 기준으로 start-end(기간) 사이 걸음수가 높은 순서대로 유저와 친구들을 조회\n" +
                    "start: 해당 주 월요일 00시 00분 00초\n" +
                    "end: 해당 주 일요일 23시 59분 59초")
    public ResponseEntity<RankResponseDto.Step> stepRank(@ModelAttribute UserRequestDto.LookUp requestDto) {
        return ResponseEntity.ok(rankService.stepRanking(requestDto));
    }
}
