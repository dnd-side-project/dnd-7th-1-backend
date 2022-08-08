package com.dnd.ground.domain.user.controller;

import com.dnd.ground.domain.user.dto.RankResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @description 메인홈 구성 컨트롤러 인터페이스
 * @author  박세헌
 * @since   2022-08-02
 * @updated 2022-08-09 / 랭킹 조회 함수 추가 : 박세헌
 */

public interface UserController {
    ResponseEntity<?> home(@RequestParam("nickName") String nickName);

    ResponseEntity<RankResponseDto.matrixRankingResponseDto> matrixRank(@RequestParam("nickname") String nickName);

    ResponseEntity<RankResponseDto.areaRankingResponseDto> areaRank(@RequestParam("nickName") String nickName);
}
