package com.dnd.ground.domain.challenge.repository;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.ChallengeColor;
import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.challenge.UserChallenge;
import com.dnd.ground.domain.user.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 박찬호
 * @description QueryDSL을 활용한 챌린지 관련 쿼리용 인터페이스
 * @since 2023-02-15
 * @updated 1.회원 닉네임, 챌린지 UUID를 통해 UC 조회하는 쿼리 생성
 *          - 2023.02.28 박찬호
 */
public interface ChallengeQueryRepository {
    List<User> findUCInProgress(User user);
    Map<User, Long> findUsersProgressChallengeCount(User user);
    Map<User, Challenge> findProgressChallengesInfo(User user);
    Map<Challenge, ChallengeColor> findChallengesColor(User user, ChallengeStatus status);
    Map<User, Long> findUsersProgressChallengeCount(Set<String> users);
    List<Challenge> findChallengesByUserInStatus(User user, ChallengeStatus status);
    UserChallenge findUC(String nickname, String uuid);
}
