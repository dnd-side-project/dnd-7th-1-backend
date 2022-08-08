package com.dnd.ground.domain.challenge.repository;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.UserChallenge;
import com.dnd.ground.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @description 회원-챌린지 간 조인엔티티와 관련한 레포지토리
 * @author  박찬호
 * @since   2022-08-03
 * @updated 1. 챌린지에 참여한 회원 정보 조회
 *          - 2022.08.08 박찬호
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

}
