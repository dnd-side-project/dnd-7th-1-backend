package com.dnd.ground.domain.challenge.repository;

import com.dnd.ground.domain.challenge.Challenge;
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
 * @updated 1. 일주일 챌린지 시작 상태 변경 기능 추가
 *          - 2022.08.09 박찬호
 */

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {
    List<UserChallenge> findByUser(User user);

    //User로 챌린지 목록 조회
    @Query("select c from Challenge c inner join UserChallenge uc on uc.user=:user")
    List<Challenge> findChallenge(@Param("user") User user);

    //User를 통해 진행 중인 챌린지가 있는 회원 조회
    @Query("select u from User u inner join UserChallenge uc on uc.user=:user where " +
            "(uc.challenge = (select c from Challenge c where c=uc.challenge and c.status='Progress') and u = :user) ")
    Optional<User> findChallenging(@Param("user") User user);

    //User를 통해 진행 중인 챌린지가 없는 회원 조회
    @Query("select u from User u inner join UserChallenge uc on uc.user=:user where " +
            "(uc.challenge = (select c from Challenge c where c=uc.challenge and c.status<>'Progress') and u = :user) ")
    Optional<User> findNotChallenging(@Param("user") User user);

    //챌린지에 포함된 회원 조회
    @Query("select uc.user from UserChallenge uc where uc.challenge=:challenge")
    List<User> findChallengeUsers(@Param("challenge") Challenge challenge);

    //유저와 챌린지를 통해 UserChallenge 조회
    Optional<UserChallenge> findByUserAndChallenge(User user, Challenge challenge);

    @Query("select uc from UserChallenge uc where uc.challenge=:challenge")
    List<UserChallenge> findUCByChallenge(@Param("challenge") Challenge challenge);

    @Modifying(clearAutomatically = true)
    @Query("delete from UserChallenge uc where uc.challenge=:challenge and (uc.status='Wait' or uc.status='Reject')")
    int deleteUCByChallenge(@Param("challenge") Challenge challenge);
}
