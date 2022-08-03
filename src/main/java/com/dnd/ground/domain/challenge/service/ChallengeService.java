package com.dnd.ground.domain.challenge.service;

import com.dnd.ground.domain.challenge.dto.ChallengeCreateRequestDto;
import org.springframework.http.ResponseEntity;

/**
 * @description 챌린지와 관련된 서비스의 역할을 분리한 인터페이스
 * @author  박찬호
 * @since   2022-08-03
 * @updated 1. 챌린지 생성 기능 구현
 *          - 2022.08.03 박찬호
 */

public interface ChallengeService {

    ResponseEntity<?> createChallenge(ChallengeCreateRequestDto challengeCreateRequestDto);
}
