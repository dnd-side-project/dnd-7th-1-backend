package com.dnd.ground.domain.matrix.service;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.exerciseRecord.dto.RankDto;
import com.dnd.ground.domain.friend.service.FriendService;
import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.exerciseRecord.dto.ExerciseCond;
import com.dnd.ground.domain.matrix.repository.MatrixRepository;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.RankResponseDto;
import com.dnd.ground.domain.user.dto.UserRequestDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import com.dnd.ground.domain.user.repository.UserRepository;
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
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated 1.클래스 역할 분리: 이름 변경(MatrixService -> RankService)
 *          2.누적 랭킹 조회 리팩토링
 *          2023-02-21 박찬호
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
    public Matrix save(Matrix matrix){
        return matrixRepository.save(matrix);
    }

    // 랭킹 조회(역대 누적 칸의 수 기준)
    public RankResponseDto.Matrix matrixRankingAllTime(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        List<User> userAndFriends = friendService.getFriends(user);
        userAndFriends.add(user);

        List<RankDto> rankMatrixRank = exerciseRecordRepository.findRankMatrixRankAllTime(new ExerciseCond(userAndFriends));

        //랭킹계산
        List<UserResponseDto.Ranking> response = new ArrayList<>();

        RankDto first = rankMatrixRank.remove(0);
        int rank = 1;
        int interval = 1;
        long prevScore = first.getScore();

        response.add(new UserResponseDto.Ranking(rank, first.getNickname(), first.getScore(), first.getPicturePath()));
        for (RankDto rankInfo : rankMatrixRank) {
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
        return new RankResponseDto.Matrix(response);
    }

    // 랭킹 조회(누적 영역의 수 기준)
    public RankResponseDto.Area areaRanking(UserRequestDto.LookUp requestDto) {

        String nickname = requestDto.getNickname();
        LocalDateTime start = requestDto.getStart();
        LocalDateTime end = requestDto.getEnd();

        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        List<User> friends = friendService.getFriends(user);  // 친구들 조회

        List<UserResponseDto.Ranking> areaRankings = new ArrayList<>();  // [랭킹, 닉네임, 영역의 수]

        // 유저의 닉네임과 영역의 수 대입
        areaRankings.add(new UserResponseDto.Ranking(1, user.getNickname(),
                (long) matrixRepository.findMatrixSetByRecords(exerciseRecordRepository.findRecord(user.getId(), start, end)).size(), user.getPicturePath()));

        // 친구들의 닉네임과 영역의 수 대입
        friends.forEach(f -> areaRankings.add(new UserResponseDto.Ranking(1, f.getNickname(),
                (long) matrixRepository.findMatrixSetByRecords(exerciseRecordRepository.findRecord(f.getId(), start, end)).size(), f.getPicturePath())));

        // 랭킹 계산 후 반환
        return new RankResponseDto.Area(calculateUserAreaRank(areaRankings, user));
    }

    /*챌린지 랭킹 조회*/
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
        for (int i=1; i<areaRankings.size(); i++){
            if (Objects.equals(areaRankings.get(i).getScore(), areaNumber)){  // 전 유저와 영역 수가 같다면 랭크 유지
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

    /*영역 기준 랭킹 계산(랭킹탭)*/
    public List<UserResponseDto.Ranking> calculateUserAreaRank(List<UserResponseDto.Ranking> areaRankings, User user) {
        //내림차순 정렬
        areaRankings.sort((a, b) -> b.getScore().compareTo(a.getScore()));

        Long areaNumber = areaRankings.get(0).getScore();  // 맨 처음 user의 영역 수
        int rank = 1;
        int count = 1;

        for (int i=1; i<areaRankings.size(); i++){

            // 전 유저와 영역 수가 같다면 랭크 유지
            if (Objects.equals(areaRankings.get(i).getScore(), areaNumber)){
                areaRankings.get(i).setRank(rank);
                count += 1;
                continue;
            }
            // 전 유저보다 영역수 가 작다면 앞에 있는 사람수 만큼이 자신 랭킹
            count += 1;
            rank = count;

            areaRankings.get(i).setRank(rank);
            areaNumber = areaRankings.get(i).getScore();  // 영역 수 update!
        }

        return areaRankings;
    }

}
