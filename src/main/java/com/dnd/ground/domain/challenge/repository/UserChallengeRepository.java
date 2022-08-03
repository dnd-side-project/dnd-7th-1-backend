package com.dnd.ground.domain.challenge.repository;

import com.dnd.ground.domain.challenge.UserChallenge;
import com.dnd.ground.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @description 회원-챌린지 간 조인엔티티와 관련한 레포지토리
 * @author  박찬호
 * @since   2022-08-03
 * @updated 1. 인터페이스 생성
 *          - 2022.08.03 박찬호
 */

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {
    List<UserChallenge> findByUser(User user);
}
