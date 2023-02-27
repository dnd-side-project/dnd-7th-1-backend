package com.dnd.ground.domain.challenge.service;

import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.challenge.dto.*;
import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * @description 챌린지와 관련된 서비스의 역할을 분리한 인터페이스
 * @author  박찬호
 * @since   2022-08-03
 * @updated 1. 시간 필드 타입 LocalDate -> LocalDateTime으로 변경
 *          - 2023.02.27
 */

public interface ChallengeService {

    ChallengeCreateResponseDto createChallenge(ChallengeCreateRequestDto challengeCreateRequestDto);
    ChallengeResponseDto.Status changeUserChallengeStatus(ChallengeRequestDto.CInfo requestDto, ChallengeStatus status);

    List<ChallengeResponseDto.Wait> findWaitChallenge(String nickname);
    List<ChallengeResponseDto.Progress> findProgressChallenge(String nickname);
    List<ChallengeResponseDto.Progress> findProgressChallenge(String userNickname, String friendNickname);
    List<ChallengeResponseDto.Done> findDoneChallenge(String nickname);
    List<ChallengeResponseDto.Invite> findInviteChallenge(String nickname);
    ChallengeResponseDto.WaitDetail getDetailWaitChallenge(ChallengeRequestDto.CInfo requestDto);
    ChallengeResponseDto.ProgressDetail getDetailProgress(ChallengeRequestDto.CInfo requestDto);
    ChallengeMapResponseDto.Detail getChallengeDetailMap(String uuid);

    List<ChallengeResponseDto.CInfoRes> findChallengeByRecord(ExerciseRecord exerciseRecord);

    Boolean deleteChallenge(ChallengeRequestDto.CInfo request);

    static LocalDateTime getSunday(LocalDateTime started) {
        return LocalDateTime.of(started.plusDays(7 - started.getDayOfWeek().getValue()).toLocalDate(), LocalTime.MAX);
    }
}
