package com.dnd.ground.domain.challenge.repository;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.ChallengeColor;
import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.challenge.UserChallenge;
import com.dnd.ground.domain.challenge.dto.ChallengeCond;
import com.dnd.ground.domain.challenge.dto.UCDto;
import com.dnd.ground.domain.user.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 박찬호
 * @description QueryDSL을 활용한 챌린지 관련 쿼리용 인터페이스
 * @since 2023-02-15
 * @updated 1.상태에 따른 챌린지 조회 쿼리를 동적 쿼리를 활용해 챌린지 조회 쿼리로 수정
 *          2.챌린지 색상 조회 쿼리의 파라미터 수정
 *          - 2023.03.03 박찬호
 */
public interface ChallengeQueryRepository {
    List<User> findUCInProgress(User user);
    Map<User, Long> findUsersProgressChallengeCount(User user);
    Map<User, Challenge> findProgressChallengesInfo(User user);
    Map<Challenge, ChallengeColor> findChallengesColor(ChallengeCond condition);
    Map<User, Long> findUsersProgressChallengeCount(Set<String> users);
    List<Challenge> findChallengesByCond(ChallengeCond condition);
    UserChallenge findUC(String nickname, String uuid);
    Map<Challenge, List<UCDto.UCInfo>> findUCInChallenge(ChallengeCond condition);
}
