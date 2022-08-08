package com.dnd.ground.domain.user.service;

import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.HomeResponseDto;
import com.dnd.ground.domain.user.dto.RankResponseDto;

/**
 * @description 유저 서비스 인터페이스
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated 2022-08-09 / 랭킹 관련 함수 추가: 박세헌
 */

public interface UserService {
    User save(User user);
    HomeResponseDto showHome(String nickname);
    RankResponseDto.matrixRankingResponseDto matrixRanking(String nickname);
    RankResponseDto.areaRankingResponseDto areaRanking(String nickname);
}
