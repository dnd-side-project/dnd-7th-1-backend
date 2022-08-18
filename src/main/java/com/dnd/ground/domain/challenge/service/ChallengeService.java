package com.dnd.ground.domain.challenge.service;

import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.challenge.dto.ChallengeCreateRequestDto;
import com.dnd.ground.domain.challenge.dto.ChallengeRequestDto;
import com.dnd.ground.domain.challenge.dto.ChallengeResponseDto;
import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.user.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * @description 챌린지와 관련된 서비스의 역할을 분리한 인터페이스
 * @author  박찬호
 * @since   2022-08-03
 * @updated 해당 운동기록이 참여하고 있는 챌린지
 *          - 2022.08.18 박세헌
 */

public interface ChallengeService {

    ResponseEntity<?> createChallenge(ChallengeCreateRequestDto challengeCreateRequestDto);
    ResponseEntity<?> changeUserChallengeStatus(ChallengeRequestDto.CInfo requestDto, ChallengeStatus status);

    void startPeriodChallenge();
    void endPeriodChallenge();

    List<ChallengeResponseDto.Wait> findWaitChallenge(String nickname);
    List<ChallengeResponseDto.Progress> findProgressChallenge(String nickname);
    List<ChallengeResponseDto.Progress> findProgressChallenge(String userNickname, String friendNickname);
    List<ChallengeResponseDto.Done> findDoneChallenge(String nickname);
    List<ChallengeResponseDto.Invite> findInviteChallenge(String nickname);

    ChallengeResponseDto.Detail getDetailProgress(ChallengeRequestDto.CInfo requestDto);

    List<ChallengeResponseDto.CInfoRes> findChallengeByRecord(ExerciseRecord exerciseRecord);
}
