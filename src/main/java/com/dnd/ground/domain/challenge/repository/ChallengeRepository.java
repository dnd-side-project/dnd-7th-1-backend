package com.dnd.ground.domain.challenge.repository;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.ChallengeStatus;
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
 * @updated 1.미사용 쿼리 삭제
 *          2.UUID로 챌린지 조회 파라미터 타입 변경(String->byte[])
 *          - 2023.03.03 박찬호
 */

public interface ChallengeRepository extends JpaRepository<Challenge, Long>, ChallengeQueryRepository {

    //UUID로 챌린지 조회
    Optional<Challenge> findByUuid(@Param("uuid") byte[] uuid);

    //진행 중인 챌린지를 제외하고, 모든 챌린지 조회 -> Progress가 아니면서 시작 날짜가 오늘인 챌린지 조회
    @Query("select c from Challenge c where c.status='WAIT' and c.started=:today")
    List<Challenge> findChallengesNotStarted(@Param("today") LocalDate today);

    //진행 중인 전체 챌린지 조회
    List<Challenge> findChallengesByStatusEquals(ChallengeStatus PROGRESS);

    //챌린지 이름으로 UUID 조회 - Dummy
    @Query("select c.uuid from Challenge c where c.name = :name")
    Optional<String> findUUIDByName(@Param("name") String name);
}