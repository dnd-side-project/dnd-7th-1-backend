package com.dnd.ground.domain.challenge.controller;

import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.challenge.dto.ChallengeCreateRequestDto;
import com.dnd.ground.domain.challenge.dto.ChallengeRequestDto;
import com.dnd.ground.domain.challenge.dto.ChallengeResponseDto;
import com.dnd.ground.domain.challenge.service.ChallengeService;
import com.dnd.ground.domain.user.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @description 챌린지와 관련된 컨트롤러 구현체
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1. 진행 대기 상태의 챌린지 조회 기능 구현
 *          2. 초대 받은 챌린지 목록 조회 기능 구현
 *          - 2022.08.13 박찬호
 */

@Api(tags = "챌린지")
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/challenge")
@RestController
public class ChallengeControllerImpl implements ChallengeController {

    private final ChallengeService challengeService;
    private final UserRepository userRepository;

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

    @GetMapping("/invite")
    @Operation(summary = "초대받은 챌린지 목록", description = "초대 받은 챌린지와 관련한 정보 목록")
    public ResponseEntity<List<ChallengeResponseDto.Invite>> findInviteChallenge(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok().body(challengeService.findInviteChallenge(nickname));
    }

//    @GetMapping("/progress")
//    @Operation(summary = "진행 중인 챌린지 리스트 조회", description = "진행 중인 챌린지 리스트+현재 순위")
//    public void findProgressChallenges(@RequestParam("nickname") String nickname) {
//        challengeService.findProgressChallenge(userRepository.findByNickname(nickname).orElseThrow());
//    }

    @GetMapping("/wait")
    @Operation(summary = "진행 대기 중인 챌린지 목록 조회", description = "대기 중 챌린지와 관련한 정보 목록")
    public ResponseEntity<List<ChallengeResponseDto.Wait>> findWaitChallenges(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok().body(challengeService.findWaitChallenge(nickname));
    }

}
