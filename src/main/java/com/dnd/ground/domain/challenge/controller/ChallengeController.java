package com.dnd.ground.domain.challenge.controller;

import com.dnd.ground.domain.challenge.dto.ChallengeCreateRequestDto;
import com.dnd.ground.domain.challenge.dto.ChallengeRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @description 챌린지와 관련된 컨트롤러의 역할을 분리한 인터페이스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1. 챌린지 수락/거절 기능 구현
 *          - 2022.08.08 박찬호
 */

public interface ChallengeController {
    ResponseEntity<?> createChallenge(@RequestBody ChallengeCreateRequestDto challengeCreateRequestDto);
    ResponseEntity<?> acceptChallenge(@RequestBody ChallengeRequestDto.CInfo requestDto);
    ResponseEntity<?> rejectChallenge(@RequestBody ChallengeRequestDto.CInfo requestDto);
}
