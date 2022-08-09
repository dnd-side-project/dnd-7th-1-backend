package com.dnd.ground.domain.challenge.service;

import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.challenge.dto.ChallengeCreateRequestDto;
import com.dnd.ground.domain.challenge.dto.ChallengeRequestDto;
import org.springframework.http.ResponseEntity;

/**
 * @description 챌린지와 관련된 서비스의 역할을 분리한 인터페이스
 * @author  박찬호
 * @since   2022-08-03
 * @updated 1. 일주일 챌린지 시작 상태 변경 기능 추가
 *          - 2022.08.09 박찬호
 */

public interface ChallengeService {

    ResponseEntity<?> createChallenge(ChallengeCreateRequestDto challengeCreateRequestDto);
    ResponseEntity<?> changeUserChallengeStatus(ChallengeRequestDto.Info requestDto, ChallengeStatus status);
    void startPeriodChallenge();

}
