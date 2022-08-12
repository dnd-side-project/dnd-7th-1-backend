package com.dnd.ground.domain.challenge.service;

import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.challenge.dto.ChallengeCreateRequestDto;
import com.dnd.ground.domain.challenge.dto.ChallengeRequestDto;
import com.dnd.ground.domain.challenge.dto.ChallengeResponseDto;
import com.dnd.ground.domain.user.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * @description 챌린지와 관련된 서비스의 역할을 분리한 인터페이스
 * @author  박찬호
 * @since   2022-08-03
 * @updated 1. 진행 대기 상태의 챌린지 조회 기능 구현
 *          - 2022.08.12 박찬호
 */

public interface ChallengeService {

    ResponseEntity<?> createChallenge(ChallengeCreateRequestDto challengeCreateRequestDto);
    ResponseEntity<?> changeUserChallengeStatus(ChallengeRequestDto.CInfo requestDto, ChallengeStatus status);
    void startPeriodChallenge();
    void endPeriodChallenge();

    void findProgressChallenge(User user);
    List<ChallengeResponseDto.Wait> findWaitChallenge(String nickname);
}
