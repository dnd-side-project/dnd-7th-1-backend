package com.dnd.ground.domain.challenge.controller;

import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.challenge.dto.*;
import com.dnd.ground.domain.challenge.service.ChallengeService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @description 챌린지와 관련된 컨트롤러 구현체
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1. 대기 중 챌린지 상세 정보 조회 API 구현
 *          2022-11-23 박찬호
 */

@Api(tags = "챌린지")
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/challenge")
@RestController
public class ChallengeControllerImpl implements ChallengeController {

    private final ChallengeService challengeService;

    @PostMapping("/")
    @Operation(summary = "챌린지 생성", description = "챌린지 생성(Type: 영역 넓히기(Widen), 칸 누적하기(Accumulate)")
    public ResponseEntity<ChallengeCreateResponseDto> createChallenge(@RequestBody ChallengeCreateRequestDto challengeCreateRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(challengeService.createChallenge(challengeCreateRequestDto));
    }

    @PostMapping("/accept")
    @Operation(summary = "챌린지 수락", description = "유저의 챌린지 수락")
    public ResponseEntity<ChallengeStatus> acceptChallenge(@RequestBody ChallengeRequestDto.CInfo requestDto) {
        return ResponseEntity.ok().body(challengeService.changeUserChallengeStatus(requestDto, ChallengeStatus.Progress));
    }

    @PostMapping("/reject")
    @Operation(summary = "챌린지 거절", description = "유저의 챌린지 거절")
    public ResponseEntity<ChallengeStatus> rejectChallenge(@RequestBody ChallengeRequestDto.CInfo requestDto) {
        return ResponseEntity.ok().body(challengeService.changeUserChallengeStatus(requestDto, ChallengeStatus.Reject));
    }

    @GetMapping("/invite")
    @Operation(summary = "초대받은 챌린지 목록", description = "초대 받은 챌린지와 관련한 정보 목록")
    public ResponseEntity<List<ChallengeResponseDto.Invite>> getInviteChallenge(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok().body(challengeService.findInviteChallenge(nickname));
    }

    @GetMapping("/wait")
    @Operation(summary = "진행 대기 중인 챌린지 목록 조회", description = "대기 중 챌린지와 관련한 정보 목록")
    public ResponseEntity<List<ChallengeResponseDto.Wait>> getWaitChallenges(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok().body(challengeService.findWaitChallenge(nickname));
    }

    @GetMapping("/progress")
    @Operation(summary = "진행 중인 챌린지 목록 조회", description = "진행 중인 챌린지 목록+현재 순위")
    public ResponseEntity<List<ChallengeResponseDto.Progress>> getProgressChallenges(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok().body(challengeService.findProgressChallenge(nickname));
    }

    @GetMapping("/done")
    @Operation(summary = "완료된 챌린지 목록 조회", description = "완료된 챌린지 목록+현재 순위")
    public ResponseEntity<List<ChallengeResponseDto.Done>> getDoneChallenges(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok().body(challengeService.findDoneChallenge(nickname));
    }

    @GetMapping("/detail/wait")
    @Operation(summary = "진행 대기 중 챌린지 상세 정보 조회", description = "infos에 들어있는 멤버 정보는 피그마에 있는 대로 주최자-본인-그외 순서니까 그대로 띄우시면 됩니다.\n본인이 주최자이면 첫번째에 그냥 나와요!")
    public ResponseEntity<ChallengeResponseDto.WaitDetail> getDetailWaitChallenge(@ModelAttribute ChallengeRequestDto.CInfo requestDto) {
        return ResponseEntity.ok(challengeService.getDetailWaitChallenge(requestDto));
    }

    @GetMapping("/detail")
    @Operation(summary = "진행 중, 완료 챌린지 상세 정보 조회", description = "챌린지와 관련된 정보 + 랭킹 관련 정보 + 개인 기록 정보")
    public ResponseEntity<ChallengeResponseDto.ProgressDetail> getDetailProgressChallenge(@ModelAttribute ChallengeRequestDto.CInfo requestDto) {
        return ResponseEntity.ok().body(challengeService.getDetailProgress(requestDto));
    }

    @GetMapping("/detail/map")
    @Operation(summary = "챌린지 상세 정보 조회: 지도", description = "챌린지 상세조회에서 지도를 클릭했을 때 보여지는 정보\n챌린지에 참여한 유저 정보+랭킹")
    public ResponseEntity<ChallengeMapResponseDto.Detail> getChallengeDetailMap(@RequestParam("uuid") String uuid) {
        return ResponseEntity.ok().body(challengeService.getChallengeDetailMap(uuid));
    }

    @PostMapping("/delete")
    @Operation(summary = "챌린지 삭제", description = "챌린지 생성자의 닉네임과 챌린지 UUID를 통해 챌린지 삭제")
    public ResponseEntity<Boolean> deleteChallenge(@RequestBody ChallengeRequestDto.CInfo request) {
        return ResponseEntity.ok().body(challengeService.deleteChallenge(request));
    }
}