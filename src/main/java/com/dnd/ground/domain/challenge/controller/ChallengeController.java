package com.dnd.ground.domain.challenge.controller;

import com.dnd.ground.domain.challenge.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * @description 챌린지와 관련된 컨트롤러의 역할을 분리한 인터페이스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1.챌린지 상세보기: 지도 API SpanDelta 적용
 *          2023-05-19 박찬호
 */

public interface ChallengeController {
    ResponseEntity<ChallengeCreateResponseDto> createChallenge(@RequestBody ChallengeCreateRequestDto challengeCreateRequestDto);
    ResponseEntity<ChallengeResponseDto.Status> acceptChallenge(@RequestBody ChallengeRequestDto.CInfo requestDto);
    ResponseEntity<ChallengeResponseDto.Status> rejectChallenge(@RequestBody ChallengeRequestDto.CInfo requestDto);
    ResponseEntity<List<ChallengeResponseDto.Wait>> getWaitChallenges(@RequestParam("nickname") String nickname);
    ResponseEntity<List<ChallengeResponseDto.Progress>> getProgressChallenges(@RequestParam("nickname") String nickname);
    ResponseEntity<List<ChallengeResponseDto.Invite>> getInviteChallenge(@RequestParam("nickname") String nickname);
    ResponseEntity<ChallengeResponseDto.ProgressDetail> getDetailProgressChallenge(@RequestBody ChallengeRequestDto.CInfo requestDto);
    ResponseEntity<ChallengeMapResponseDto.Detail> getChallengeDetailMap(@Valid @ModelAttribute ChallengeMapRequestDto request);
}
