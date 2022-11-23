package com.dnd.ground.domain.challenge.controller;

import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.challenge.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @description 챌린지와 관련된 컨트롤러의 역할을 분리한 인터페이스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1. 대기 중 챌린지 상세 정보 조회 API 구현
 *          2022-11-23 박찬호
 */

public interface ChallengeController {
    ResponseEntity<ChallengeCreateResponseDto> createChallenge(@RequestBody ChallengeCreateRequestDto challengeCreateRequestDto);
    ResponseEntity<ChallengeStatus> acceptChallenge(@RequestBody ChallengeRequestDto.CInfo requestDto);
    ResponseEntity<ChallengeStatus> rejectChallenge(@RequestBody ChallengeRequestDto.CInfo requestDto);
    ResponseEntity<List<ChallengeResponseDto.Wait>> getWaitChallenges(@RequestParam("nickname") String nickname);
    ResponseEntity<List<ChallengeResponseDto.Progress>> getProgressChallenges(@RequestParam("nickname") String nickname);
    ResponseEntity<List<ChallengeResponseDto.Invite>> getInviteChallenge(@RequestParam("nickname") String nickname);
    ResponseEntity<ChallengeResponseDto.ProgressDetail> getDetailProgressChallenge(@RequestBody ChallengeRequestDto.CInfo requestDto);
    ResponseEntity<ChallengeMapResponseDto.Detail> getChallengeDetailMap(@RequestParam("uuid") String uuid);
}
