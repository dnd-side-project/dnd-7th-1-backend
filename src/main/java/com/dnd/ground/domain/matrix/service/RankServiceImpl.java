package com.dnd.ground.domain.matrix.service;

import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.exerciseRecord.dto.RankDto;
import com.dnd.ground.domain.friend.service.FriendService;
import com.dnd.ground.domain.exerciseRecord.dto.RankCond;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.RankResponseDto;
import com.dnd.ground.domain.user.dto.UserRequestDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.CommonException;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.exception.UserException;
import lombok.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @description 운동 영역 서비스 클래스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1. 특정 회원의 누적 랭킹 조회 추가
 *          - 2023.03.03
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankServiceImpl implements RankService {
    private final UserRepository userRepository;
    private final FriendService friendService;
    private final ExerciseRecordRepository exerciseRecordRepository;

    //역대 누적 랭킹 조회
    public RankResponseDto.Matrix matrixRankingAllTime(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        List<User> userAndFriends = friendService.getFriends(user);
        userAndFriends.add(user);

        List<RankDto> matrixRank = exerciseRecordRepository.findRankMatrixRankAllTime(new RankCond(userAndFriends));
        return new RankResponseDto.Matrix(calculateUsersRank(matrixRank));
    }

    //특정 유저의 역대 누적 랭킹 조회
    public UserResponseDto.Ranking matrixUserRankingAllTime(User user) {
        List<User> userAndFriends = friendService.getFriends(user);
        userAndFriends.add(user);

        List<RankDto> matrixRank = exerciseRecordRepository.findRankMatrixRankAllTime(new RankCond(userAndFriends));
        return calculateUserRank(matrixRank, user);
    }

    //영역 랭킹 조회
    @Override
    public RankResponseDto.Area areaRanking(UserRequestDto.LookUp requestDto) {
        LocalDateTime started = requestDto.getStarted();
        LocalDateTime ended = requestDto.getEnded();

        User user = userRepository.findByNickname(requestDto.getNickname()).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        List<User> userAndFriends = friendService.getFriends(user);
        userAndFriends.add(user);
        List<RankDto> areaRank = exerciseRecordRepository.findRankArea(new RankCond(userAndFriends, started, ended));
        return new RankResponseDto.Area(calculateUsersRank(areaRank));
    }

    //걸음수 랭킹 조회
    @Override
    public RankResponseDto.Step stepRanking(UserRequestDto.LookUp requestDto) {
        LocalDateTime start = requestDto.getStarted();
        LocalDateTime end = requestDto.getEnded();

        User user = userRepository.findByNickname(requestDto.getNickname()).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        List<User> userAndFriends = friendService.getFriends(user);
        userAndFriends.add(user);
        List<RankDto> result = exerciseRecordRepository.findRankStep(new RankCond(userAndFriends, start, end));
        return new RankResponseDto.Step(calculateUsersRank(result));
    }

    //점수 기준 랭킹 계산
    @Override
    public List<UserResponseDto.Ranking> calculateUsersRank(List<RankDto> ranks) {
        List<UserResponseDto.Ranking> response = new ArrayList<>();
        Collections.sort(ranks);

        RankDto first = ranks.remove(0);
        int rank = 1;
        int interval = 1;
        long prevScore = first.getScore();
        response.add(new UserResponseDto.Ranking(rank, first.getNickname(), first.getScore(), first.getPicturePath()));

        for (RankDto rankInfo : ranks) {
            Long score = rankInfo.getScore();
            if (score < prevScore) {
                rank += interval;
                prevScore = score;
                interval = 1;
            } else {
                interval++;
            }

            response.add(new UserResponseDto.Ranking(rank, rankInfo.getNickname(), score, rankInfo.getPicturePath()));
        }
        return response;
    }

    @Override
    public UserResponseDto.Ranking calculateUserRank(List<RankDto> ranks, User targetUser) {
        RankDto first = ranks.remove(0);
        if (first.getNickname().equals(targetUser.getNickname()))
            return new UserResponseDto.Ranking(1, targetUser.getNickname(), first.getScore(), targetUser.getPicturePath());

        int rank = 1;
        int interval = 1;
        long prevScore = first.getScore();

        for (RankDto rankInfo : ranks) {
            Long score = rankInfo.getScore();
            if (score < prevScore) {
                rank += interval;
                prevScore = score;
                interval = 1;
            } else {
                interval++;
            }

            if (rankInfo.getNickname().equals(targetUser.getNickname()))
                return new UserResponseDto.Ranking(rank, targetUser.getNickname(), score, targetUser.getPicturePath());
        }
        throw new CommonException(ExceptionCodeSet.RANKING_CAL_FAIL);
    }
}