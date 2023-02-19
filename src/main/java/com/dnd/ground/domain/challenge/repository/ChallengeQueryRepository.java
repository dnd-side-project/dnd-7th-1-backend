package com.dnd.ground.domain.challenge.repository;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.ChallengeColor;
import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.user.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 박찬호
 * @description QueryDSL을 활용한 챌린지 관련 쿼리용 인터페이스
 * @since 2023-02-15
 * @updated 1. 챌린지 색깔 조회 쿼리 결과 Map으로 변경
 *          2. 회원이 참여하고 있는 챌린지 조회 쿼리 생성
 *          - 2023.02.19 박찬호
 */
public interface ChallengeQueryRepository {
    List<User> findUCInProgress(User user);
    Map<User, Long> findUsersProgressChallengeCount(User user);
    Map<User, Challenge> findProgressChallengesInfo(User user);
    Map<Challenge, ChallengeColor> findChallengesColor(User user, ChallengeStatus status);
    Map<User, Long> findUsersProgressChallengeCount(Set<String> users);
    List<Challenge> findChallengesByUserInStatus(User user, ChallengeStatus status);
}
