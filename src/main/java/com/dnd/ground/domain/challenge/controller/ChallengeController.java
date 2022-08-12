package com.dnd.ground.domain.challenge.controller;

import com.dnd.ground.domain.challenge.dto.ChallengeCreateRequestDto;
import com.dnd.ground.domain.challenge.dto.ChallengeRequestDto;
import com.dnd.ground.domain.challenge.dto.ChallengeResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @description 챌린지와 관련된 컨트롤러의 역할을 분리한 인터페이스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1. 진행 대기 상태의 챌린지 조회 기능 구현
 *          2. 초대 받은 챌린지 목록 조회 기능 구현
 *          - 2022.08.13 박찬호
 */

public interface ChallengeController {
    ResponseEntity<?> createChallenge(@RequestBody ChallengeCreateRequestDto challengeCreateRequestDto);
    ResponseEntity<?> acceptChallenge(@RequestBody ChallengeRequestDto.CInfo requestDto);
    ResponseEntity<?> rejectChallenge(@RequestBody ChallengeRequestDto.CInfo requestDto);
    ResponseEntity<List<ChallengeResponseDto.Wait>> findWaitChallenges(@RequestParam("nickname") String nickname);
    ResponseEntity<List<ChallengeResponseDto.Invite>> findInviteChallenge(@RequestParam("nickname") String nickname);
}
