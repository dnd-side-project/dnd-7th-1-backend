package com.dnd.ground.domain.challenge.repository;

import com.dnd.ground.domain.challenge.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @description 챌린지와 관련한 레포지토리
 * @author  박찬호
 * @since   2022-08-03
 * @updated 1. 인터페이스 생성
 *          - 2022.08.03 박찬호
 */

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
}
