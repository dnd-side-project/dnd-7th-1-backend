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

/**
 * @description 메인홈 구성 컨트롤러 인터페이스
 * @author  박세헌, 박찬호
 * @since   2022-08-02
 * @updated 1. 랭킹 관련 메소드 이동(UserService -> MatrixService)
 *          - 2022.08.11 박찬호
 */

@Api(tags = "운동 영역")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/matrix")
@RestController
public class MatrixControllerImpl implements MatrixController {

    private final MatrixService matrixService;

    @Operation(summary = "칸의 수 랭킹", description = "칸의 수가 높은 순서대로 유저들을 조회")
    @GetMapping("/rank/accumulate")
    public ResponseEntity<RankResponseDto.matrixRankingResponseDto> matrixRank(@RequestParam("nickname") String nickName){
        return ResponseEntity.ok(matrixService.matrixRanking(nickName));
    }

    @Operation(summary = "영역의 수 랭킹", description = "영역의 수가 높은 순서대로 유저들을 조회")
    @GetMapping("/rank/widen")
    public ResponseEntity<RankResponseDto.areaRankingResponseDto> areaRank(@RequestParam("nickname") String nickName){
        return ResponseEntity.ok(matrixService.areaRanking(nickName));
    }
}
