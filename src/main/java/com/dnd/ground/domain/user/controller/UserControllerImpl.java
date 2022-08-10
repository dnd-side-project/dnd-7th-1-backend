package com.dnd.ground.domain.user.controller;

import com.dnd.ground.domain.user.dto.HomeResponseDto;
import com.dnd.ground.domain.user.dto.RankResponseDto;
import com.dnd.ground.domain.user.service.UserService;
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
 * @description 메인홈 구성 컨트롤러 클래스
 * @author  박세헌
 * @since   2022-08-02
 * @updated 2022-08-09 / 랭킹 조회 api 추가: 박세헌
 */

@Api(tags = "유저")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @GetMapping("/home")
    @Operation(summary = "홈 화면 조회",
            description = "닉네임을 통해 홈화면에 필요한 유저 정보(userMatrix), " +
                    "챌린지를 안하는 친구 정보(friendMatrices, 리스트), " +
                    "나와 챌린지를 하는 유저 정보(challengeMatrices, 리스트) 조회")
    public ResponseEntity<HomeResponseDto> home(@RequestParam("nickname") String nickName){
        return ResponseEntity.ok(userService.showHome(nickName));
    }

    @Operation(summary = "칸의 수 랭킹", description = "칸의 수가 높은 순서대로 유저들을 조회")
    @GetMapping("/rank/matrix")
    public ResponseEntity<RankResponseDto.matrixRankingResponseDto> matrixRank(@RequestParam("nickname") String nickName){
        return ResponseEntity.ok(userService.matrixRanking(nickName));
    }

    @Operation(summary = "영역의 수 랭킹", description = "영역의 수가 높은 순서대로 유저들을 조회")
    @GetMapping("/rank/area")
    public ResponseEntity<RankResponseDto.areaRankingResponseDto> areaRank(@RequestParam("nickname") String nickName){
        return ResponseEntity.ok(userService.areaRanking(nickName));
    }

}
