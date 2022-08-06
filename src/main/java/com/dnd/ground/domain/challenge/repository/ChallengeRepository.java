package com.dnd.ground.domain.challenge.repository;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @description 챌린지와 관련한 레포지토리
 * @author  박찬호
 * @since   2022-08-03
 * @updated 1. 회원이 진행 중인 챌린지 개수 조회
 *          2. 친구와 함께 진행 중인 챌린지 개수 조회
 *          - 2022.08.05 박찬호
 */

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    //진행 중인 챌린지 개수
    @Query("select count(c) from Challenge c inner join UserChallenge uc on uc.challenge=c where " +
            "uc.user=:user and c.status='Progress'")
    Integer getCountChallenge(@Param("user") User user);

    //친구와 함께 진행 중인 챌린지 개수
    @Query("select count(c.id) from Challenge c where c IN (select uc.challenge from UserChallenge uc where uc.user=:user) and " +
            "c.status='Progress' and c = (select uc.challenge from UserChallenge uc where uc.challenge=c and uc.user =:friend)")
    Integer getCountChallenge(@Param("user")User user, @Param("friend") User friend);
}