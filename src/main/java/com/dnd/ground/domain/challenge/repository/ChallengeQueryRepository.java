package com.dnd.ground.domain.challenge.repository;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.dto.ChallengeColorDto;
import com.dnd.ground.domain.user.User;

import java.util.List;
import java.util.Map;
/**
 * @author 박찬호
 * @description QueryDSL을 활용한 챌린지 관련 쿼리용 인터페이스
 * @since 2023-02-15
 * @updated 1.인터페이스 생성
 *          - 2023.02.15 박찬호
 */
public interface ChallengeQueryRepository {
    List<User> findUCInProgress(User user);
    Map<User, Long> findProgressChallengeCount(User user);
    Map<User, Challenge> findProgressChallengesInfo(User user);
    List<ChallengeColorDto> findProgressChallengesColor(User user);
}
