package com.dnd.ground.domain.matrix.matrixService;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.friend.service.FriendService;
import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.matrix.matrixRepository.MatrixRepository;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.RankResponseDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import com.dnd.ground.domain.user.repository.UserRepository;
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
 * @author  박세헌
 * @since   2022-08-01
 * @updated 1. 랭킹 계산 메소드 모듈화
 *          - 2022.08.17 박찬호
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatrixServiceImpl implements MatrixService {
    private final MatrixRepository matrixRepository;
    private final UserRepository userRepository;
    private final FriendService friendService;
    private final ExerciseRecordRepository exerciseRecordRepository;
    private final UserChallengeRepository userChallengeRepository;

    @Transactional
    public Matrix save(Matrix matrix){
        return matrixRepository.save(matrix);
    }

    // 랭킹 조회(누적 칸의 수 기준)(보류)
    public RankResponseDto.Matrix matrixRanking(String nickname, LocalDateTime start, LocalDateTime end) {
        User user = userRepository.findByNickname(nickname).orElseThrow();
        List<User> userAndFriends = friendService.getFriends(user);  // 친구들 조회
        userAndFriends.add(0, user);  // 유저 추가

        // [Tuple(닉네임, 이번주 누적 칸수)] 칸수 기준 내림차순 정렬
        List<Tuple> matrixCount = exerciseRecordRepository.findMatrixCount(userAndFriends, start, end);

        // 랭킹 계산[랭킹, 닉네임, 칸의 수]
        List<UserResponseDto.Ranking> matrixRankings = calculateMatrixRank(matrixCount, userAndFriends);

        return new RankResponseDto.Matrix(matrixRankings);
    }

    // 랭킹 조회(누적 영역의 수 기준)
    public RankResponseDto.Area areaRanking(String nickname, LocalDateTime start, LocalDateTime end) {
        User user = userRepository.findByNickname(nickname).orElseThrow();
        List<User> friends = friendService.getFriends(user);  // 친구들 조회
        List<UserResponseDto.Ranking> areaRankings = new ArrayList<>();  // [랭킹, 닉네임, 영역의 수]

        // 유저의 닉네임과 영역의 수 대입
        areaRankings.add(new UserResponseDto.Ranking(1, user.getNickname(),
                (long) matrixRepository.findMatrixSetByRecords(exerciseRecordRepository.findRecord(user.getId(), start, end)).size()));

        // 친구들의 닉네임과 영역의 수 대입
        friends.forEach(f -> areaRankings.add(new UserResponseDto.Ranking(1, f.getNickname(),
                (long) matrixRepository.findMatrixSetByRecords(exerciseRecordRepository.findRecord(f.getId(), start, end)).size())));
        
        // 랭킹 계산 후 반환
        return new RankResponseDto.Area(calculateAreaRank(areaRankings));
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
                            (long) matrixRepository.findMatrixSetByRecords(exerciseRecordRepository.findRecord(m.getId(), start, end)).size())
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
                            (Long) info.get(1)));
                    count += 1;
                    continue;
                }
                // 전 유저보다 작다면 랭크+1
                rank += 1;
                matrixRankings.add(new UserResponseDto.Ranking(rank, (String) info.get(0),
                        (Long) info.get(1)));
                matrixNumber = (Long) info.get(1);  // 칸 수 update!
                count += 1;
            }
            rank += 1;
            // 나머지 0점인 유저들 추가
            for (int i = count; i < member.size(); i++) {
                matrixRankings.add(new UserResponseDto.Ranking(rank, member.get(i).getNickname(), 0L));
            }
        }

        // 전부다 0점이라면
        else {
            for (int i = count; i < member.size(); i++) {
                matrixRankings.add(new UserResponseDto.Ranking(rank, member.get(i).getNickname(), 0L));
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

        for (int i=1; i<areaRankings.size(); i++){
            if (Objects.equals(areaRankings.get(i).getScore(), areaNumber)){  // 전 유저와 영역 수가 같다면 랭크 유지
                areaRankings.get(i).setRank(rank);
                continue;
            }
            // 전 유저보다 영역 수가 작다면 랭크+1
            rank += 1;
            areaRankings.get(i).setRank(rank);
            areaNumber = areaRankings.get(i).getScore();  // 영역 수 update!
        }

        return areaRankings;
    }
}
