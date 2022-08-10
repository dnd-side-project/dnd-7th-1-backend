package com.dnd.ground.domain.challenge.controller;

import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.challenge.dto.ChallengeCreateRequestDto;
import com.dnd.ground.domain.challenge.dto.ChallengeRequestDto;
import com.dnd.ground.domain.challenge.service.ChallengeService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * @description 챌린지와 관련된 컨트롤러 구현체
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1. 챌린지 수락/거절 기능 구현
 *          - 2022.08.08 박찬호
 */

@Api(tags = "챌린지")
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/challenge")
@RestController
public class ChallengeControllerImpl implements ChallengeController {

    private final ChallengeService challengeService;

    @PostMapping("/")
    @Operation(summary = "챌린지 생성", description = "챌린지 생성")
    public ResponseEntity<?> createChallenge(@RequestBody ChallengeCreateRequestDto challengeCreateRequestDto) {
        return challengeService.createChallenge(challengeCreateRequestDto);
    }

    @PostMapping("/accept")
    @Operation(summary = "챌린지 수락", description = "유저의 챌린지 수락")
    public ResponseEntity<?> acceptChallenge(@RequestBody ChallengeRequestDto.CInfo requestDto) {
        return challengeService.changeUserChallengeStatus(requestDto, ChallengeStatus.Progress);
    }

    @PostMapping("/reject")
    @Operation(summary = "챌린지 거절", description = "유저의 챌린지 거절")
    public ResponseEntity<?> rejectChallenge(@RequestBody ChallengeRequestDto.CInfo requestDto) {
        return challengeService.changeUserChallengeStatus(requestDto, ChallengeStatus.Reject);
    }


}
