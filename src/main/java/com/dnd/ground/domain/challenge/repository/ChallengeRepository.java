package com.dnd.ground.domain.challenge.repository;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @description 챌린지와 관련한 레포지토리
 * @author  박찬호
 * @since   2022-08-03
 * @updated 1. 진행 중인 챌린지 정보 조회 추가
 *          - 2022.08.08 박찬호
 */

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    //UUID로 챌린지 조회
    Challenge findByUuid(@Param("uuid") String uuid);

    //진행 중인 챌린지 정보 조회
    @Query("select c from Challenge c inner join UserChallenge uc on uc.challenge=c where uc.user=:user and c.status='Progress'")
    List<Challenge> findChallenge(@Param("user") User user);

    //진행 중인 챌린지 개수
    @Query("select count(c) from Challenge c inner join UserChallenge uc on uc.challenge=c where " +
            "uc.user=:user and c.status='Progress'")
    Integer findCountChallenge(@Param("user") User user);

    //친구와 함께 진행 중인 챌린지 개수
    @Query("select count(c.id) from Challenge c where c IN (select uc.challenge from UserChallenge uc where uc.user=:user) and " +
            "c.status='Progress' and c = (select uc.challenge from UserChallenge uc where uc.challenge=c and uc.user =:friend)")
    Integer findCountChallenge(@Param("user")User user, @Param("friend") User friend);

    //친구와 함께 진행 중인 챌린지 정보 조회
    @Query("select c from Challenge c where c IN (select uc.challenge from UserChallenge uc where uc.user=:user) and " +
            "c.status='Progress' and c = (select uc.challenge from UserChallenge uc where uc.challenge=c and uc.user =:friend) order by c.id ASC")
    List<Challenge> findChallengesWithFriend(@Param("user")User user, @Param("friend") User friend);
}