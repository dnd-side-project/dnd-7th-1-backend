package com.dnd.ground.domain.challenge.repository;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @description 챌린지와 관련한 레포지토리
 * @author  박찬호
 * @since   2022-08-03
 * @updated 조인 시 일부 조건이 빠져있던 문제 해결
 *          - 2022.10.29 박찬호
 */

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    //UUID로 챌린지 조회
    Optional<Challenge> findByUuid(@Param("uuid") String uuid);

    //진행 중인 챌린지 목록 정보 조회
    @Query("select c from Challenge c inner join UserChallenge uc on uc.challenge=c where uc.user=:user and c.status='Progress' order by c.started ASC")
    List<Challenge> findProgressChallenge(@Param("user") User user);

    //진행대기 중인 챌린지 목록 정보 조회
    @Query("select c from Challenge c inner join UserChallenge uc on uc.challenge=c where uc.user=:user and c.status='Wait' order by c.started ASC")
    List<Challenge> findWaitChallenge(@Param("user") User user);

    //완료된 챌린지 목록 정보 조회
    @Query("select c from Challenge c inner join UserChallenge uc on uc.challenge=c where uc.user=:user and c.status='Done' order by c.started ASC")
    List<Challenge> findDoneChallenge(@Param("user") User user);
     
    //진행 중인 챌린지 개수
    @Query("select count(c) from Challenge c inner join UserChallenge uc on uc.challenge=c where " +
            "uc.user=:user and c.status='Progress'")
    Integer findCountChallenge(@Param("user") User user);

    //친구와 함께 진행 중인 챌린지 개수
    @Query("select count(c.id) from Challenge c where c IN (select uc.challenge from UserChallenge uc where uc.user=:user and uc.challenge=c) and " +
            "c.status='Progress' and c = (select uc.challenge from UserChallenge uc where uc.challenge=c and uc.user =:friend)")
    Integer findCountChallenge(@Param("user")User user, @Param("friend") User friend);

    //친구와 함께 진행 중인 챌린지 정보 조회
    @Query("select c from Challenge c where c IN (select uc.challenge from UserChallenge uc where uc.user=:user and uc.challenge=c) and " +
            "c.status='Progress' and c = (select uc.challenge from UserChallenge uc where uc.challenge=c and uc.user =:friend) order by c.id ASC")
    List<Challenge> findChallengesWithFriend(@Param("user")User user, @Param("friend") User friend);

    //진행 중인 챌린지를 제외하고, 모든 챌린지 조회 -> Progress가 아니면서 시작 날짜가 오늘인 챌린지 조회
    @Query("select c from Challenge c where c.status<>'Progress' and c.started=:today")
    List<Challenge> findChallengesNotStarted(@Param("today") LocalDate today);

    //진행 중인 전체 챌린지 조회
    List<Challenge> findChallengesByStatusEquals(ChallengeStatus Progress);

    //초대 받은 챌린지 조회(UC가 Wait 상태인 챌린지 조회)
    @Query("select c from Challenge c inner join UserChallenge uc on uc.user=:user and uc.challenge=c where uc.status='Wait' order by c.created ASC")
    List<Challenge> findChallengeInWait(@Param("user") User user);

    //챌린지 시작 시간이 start~end 사이인 챌린지 조회
    @Query("select c from Challenge c inner join UserChallenge uc on uc.challenge = c where (uc.status='Progress' or uc.status='Done' or uc.status='MasterDone') " +
            "and uc.user = :user and c.started between :start and :end")
    List<Challenge> findChallengesBetweenStartAndEnd(@Param("user") User user,
                                                     @Param("start") LocalDate start,
                                                     @Param("end") LocalDate end);
}