package com.dnd.ground.domain.matrix.service;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.exerciseRecord.dto.RankDto;
import com.dnd.ground.domain.friend.service.FriendService;
import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.exerciseRecord.dto.RankCond;
import com.dnd.ground.domain.matrix.repository.MatrixRepository;
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

import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description 운동 영역 서비스 클래스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1. 특정 회원의 랭킹 계산 메소드 작성
 *          - 2023.02.28
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankServiceImpl implements RankService {
    private final MatrixRepository matrixRepository;
    private final UserRepository userRepository;
    private final FriendService friendService;
    private final ExerciseRecordRepository exerciseRecordRepository;
    private final UserChallengeRepository userChallengeRepository;

    @Transactional
    public Matrix save(Matrix matrix) {
        return matrixRepository.save(matrix);
    }

    //역대 누적 랭킹 조회
    public RankResponseDto.Matrix matrixRankingAllTime(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        List<User> userAndFriends = friendService.getFriends(user);
        userAndFriends.add(user);

        List<RankDto> matrixRank = exerciseRecordRepository.findRankMatrixRankAllTime(new RankCond(userAndFriends));
        return new RankResponseDto.Matrix(calculateUsersRank(matrixRank));
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

    /*챌린지 랭킹 조회*/
    @Override
    public RankResponseDto.Area challengeRank(Challenge challenge, LocalDateTime start, LocalDateTime end) {
        List<User> member = userChallengeRepository.findChallengeUsers(challenge);//챌린지에 참여하는 회원 리스트
        List<UserResponseDto.Ranking> areaRankings = new ArrayList<>();

        for (User m : member) {
            areaRankings.add(
                    new UserResponseDto.Ranking(
                            1,
                            m.getNickname(),
                            (long) matrixRepository.findMatrixSetByRecords(exerciseRecordRepository.findRecord(m.getId(), start, end)).size(),
                            m.getPicturePath())
            );
        }

        //랭킹 계산 후 반환
        return new RankResponseDto.Area(calculateAreaRank(areaRankings));
    }

    /*칸 수 기준 랭킹 계산*/
    public List<UserResponseDto.Ranking> calculateMatrixRank(List<Tuple> matrixCount, List<User> member) {
        List<UserResponseDto.Ranking> matrixRankings = new ArrayList<>();
        int count = 0;
        int rank = 1;

        // 1명이라도 0점이 아니라면
        if (!matrixCount.isEmpty()) {
            Long matrixNumber = (Long) matrixCount.get(0).get(1);  // 맨 처음 user의 칸 수
            for (Tuple info : matrixCount) {
                if (Objects.equals(info.get(1), matrixNumber)) {  // 전 유저와 칸수가 같다면 랭크 유지
                    matrixRankings.add(new UserResponseDto.Ranking(rank, (String) info.get(0),
                            (Long) info.get(1), (String) info.get(2)));
                    count += 1;
                    continue;
                }
                // 전 유저보다 작다면 랭크+1
                count += 1;
                rank = count;
                matrixRankings.add(new UserResponseDto.Ranking(rank, (String) info.get(0),
                        (Long) info.get(1), (String) info.get(2)));
                matrixNumber = (Long) info.get(1);  // 칸 수 update!
            }
            rank += 1;
            // 나머지 0점인 유저들 추가
            for (int i = count; i < member.size(); i++) {
                matrixRankings.add(new UserResponseDto.Ranking(rank, member.get(i).getNickname(), 0L, member.get(i).getPicturePath()));
            }
        }

        // 전부다 0점이라면
        else {
            for (int i = count; i < member.size(); i++) {
                matrixRankings.add(new UserResponseDto.Ranking(rank, member.get(i).getNickname(), 0L, member.get(i).getPicturePath()));
            }
        }

        return matrixRankings;
    }

    /*영역 기준 랭킹 계산*/
    public List<UserResponseDto.Ranking> calculateAreaRank(List<UserResponseDto.Ranking> areaRankings) {
        //내림차순 정렬
        areaRankings.sort((a, b) -> b.getScore().compareTo(a.getScore()));

        Long areaNumber = areaRankings.get(0).getScore();  // 맨 처음 user의 영역 수
        int rank = 1;
        int count = 1;
        for (int i = 1; i < areaRankings.size(); i++) {
            if (Objects.equals(areaRankings.get(i).getScore(), areaNumber)) {  // 전 유저와 영역 수가 같다면 랭크 유지
                areaRankings.get(i).setRank(rank);
                count += 1;
                continue;
            }
            // 전 유저보다 영역 수가 작다면 랭크+1
            count += 1;
            rank = count;
            areaRankings.get(i).setRank(rank);
            areaNumber = areaRankings.get(i).getScore();  // 영역 수 update!
        }

        return areaRankings;
    }

    //점수 기준 랭킹 계산
    @Override
    public List<UserResponseDto.Ranking> calculateUsersRank(List<RankDto> ranks) {
        List<UserResponseDto.Ranking> response = new ArrayList<>();

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