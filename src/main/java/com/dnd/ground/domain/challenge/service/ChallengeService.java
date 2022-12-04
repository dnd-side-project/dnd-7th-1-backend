package com.dnd.ground.domain.challenge.service;

import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.challenge.dto.*;
import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * @description 챌린지와 관련된 서비스의 역할을 분리한 인터페이스
 * @author  박찬호, 박세헌
 * @since   2022-08-03
 * @updated 1. 대기 중 챌린지 상세 정보 조회 API 구현
 *          2022-11-23 박찬호
 */

public interface ChallengeService {

    ChallengeCreateResponseDto createChallenge(ChallengeCreateRequestDto challengeCreateRequestDto);
    ChallengeStatus changeUserChallengeStatus(ChallengeRequestDto.CInfo requestDto, ChallengeStatus status);

    void startPeriodChallenge();
    void endPeriodChallenge();

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
}
