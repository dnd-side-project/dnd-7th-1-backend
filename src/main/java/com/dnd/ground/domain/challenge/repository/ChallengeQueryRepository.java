package com.dnd.ground.domain.challenge.repository;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.ChallengeColor;
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
 * @updated 1.진행 중, 진행 대기 상태 == 참여(Join) 한 챌린지 개수 구하는 쿼리 이름 변경: findUsersJoinChallengeCount
 *          2.회원의 참여중인 챌린지 개수 구하는 쿼리 생성
 *          - 2023.05.22 박찬호
 */
public interface ChallengeQueryRepository {
    List<User> findUCInProgress(User user);
    Map<User, Long> findUserProgressChallengeCount(User user);
    Map<User, Challenge> findProgressChallengesInfo(User user);
    Map<Challenge, ChallengeColor> findChallengesColor(ChallengeCond condition);
    Integer findUserJoinChallengeCount(User target);
    Map<User, Long> findUsersJoinChallengeCount(Set<String> users);
    List<Challenge> findChallengesByCond(ChallengeCond condition);
    UserChallenge findUC(String nickname, String uuid);
    Map<Challenge, List<UCDto.UCInfo>> findUCInChallenge(ChallengeCond condition);
}
