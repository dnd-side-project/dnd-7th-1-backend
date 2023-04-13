package com.dnd.ground.domain.challenge.repository;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.ChallengeColor;
import com.dnd.ground.domain.challenge.UserChallenge;
import com.dnd.ground.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @description 회원-챌린지 간 조인엔티티와 관련한 레포지토리
 * @author  박찬호
 * @since   2022-08-03
 * @updated 1. 미사용 쿼리 제거
 *          - 2023.02.28 박찬호
 */

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long>, ChallengeQueryRepository {
    //챌린지에 포함된 회원 조회
    @Query("select uc.user from UserChallenge uc where uc.challenge=:challenge")
    List<User> findChallengeUsers(@Param("challenge") Challenge challenge);

    //유저와 챌린지를 통해 UserChallenge 조회
    Optional<UserChallenge> findByUserAndChallenge(User user, Challenge challenge);

    //해당 챌린지의 UC 조회
    List<UserChallenge> findByChallenge(@Param("challenge") Challenge challenge);

    //대기 중, 거절 상태의 UC 삭제
    @Modifying(clearAutomatically = true)
    @Query("delete from UserChallenge uc where uc.challenge=:challenge and (uc.status='Wait' or uc.status='Reject')")
    int deleteUCByChallenge(@Param("challenge") Challenge challenge);

    //챌린지의 주최자 조회
    @Query("select uc.user from UserChallenge uc where uc.challenge=:challenge and uc.status='Master'")
    User findMasterInChallenge(@Param("challenge") Challenge challenge);

    //챌린지 색깔 조회
    @Query("select uc.color from UserChallenge uc where uc.user=:user and uc.challenge=:challenge")
    ChallengeColor findChallengeColor(@Param("user") User user, @Param("challenge") Challenge challenge);

    //챌린지-회원 관계 테이블에 데이터가 있는 회원 조회
    @Query("select uc from UserChallenge uc where uc.user=:user")
    List<UserChallenge> findUCs(@Param("user") User user);

}
