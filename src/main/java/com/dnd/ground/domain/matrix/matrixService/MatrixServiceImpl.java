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
import com.dnd.ground.global.exception.CNotFoundException;
import com.dnd.ground.global.exception.CommonErrorCode;
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
 * @updated 1. orElseThrow() 예외 처리 - 2022.08.18 박찬호
 *          2. 랭킹 동점 로직, 유저 맨위 로직 - 2022.08.25 박세헌
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

    // 랭킹 조회(역대 누적 칸의 수 기준)
    public RankResponseDto.Matrix matrixRanking(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER));

        List<User> userAndFriends = friendService.getFriends(user);  // 친구들 조회
        userAndFriends.add(user);  // 유저 추가

        LocalDateTime start = user.getCreated();
        LocalDateTime end = LocalDateTime.now();

        // [Tuple(닉네임, 이번주 누적 칸수)] 칸수 기준 내림차순 정렬
        List<Tuple> matrixCount = exerciseRecordRepository.findMatrixCount(userAndFriends, start, end);

        // 랭킹 계산[랭킹, 닉네임, 칸의 수]
        List<UserResponseDto.Ranking> matrixRankings = calculateUserMatrixRank(matrixCount, userAndFriends, user);

        return new RankResponseDto.Matrix(matrixRankings);
    }

    // 랭킹 조회(누적 영역의 수 기준)
    public RankResponseDto.Area areaRanking(String nickname, LocalDateTime start, LocalDateTime end) {
        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER));

        List<User> friends = friendService.getFriends(user);  // 친구들 조회

        List<UserResponseDto.Ranking> areaRankings = new ArrayList<>();  // [랭킹, 닉네임, 영역의 수]

        // 유저의 닉네임과 영역의 수 대입
        areaRankings.add(new UserResponseDto.Ranking(1, user.getNickname(),
                (long) matrixRepository.findMatrixSetByRecords(exerciseRecordRepository.findRecord(user.getId(), start, end)).size()));

        // 친구들의 닉네임과 영역의 수 대입
        friends.forEach(f -> areaRankings.add(new UserResponseDto.Ranking(1, f.getNickname(),
                (long) matrixRepository.findMatrixSetByRecords(exerciseRecordRepository.findRecord(f.getId(), start, end)).size())));
        
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
                count += 1;
                rank = count;
                matrixRankings.add(new UserResponseDto.Ranking(rank, (String) info.get(0),
                        (Long) info.get(1)));
                matrixNumber = (Long) info.get(1);  // 칸 수 update!
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

    /*칸 수 기준 랭킹 계산(랭킹탭)*/
    public List<UserResponseDto.Ranking> calculateUserMatrixRank(List<Tuple> matrixCount, List<User> member, User user) {
        List<UserResponseDto.Ranking> matrixRankings = new ArrayList<>();
        int count = 0;
        int rank = 1;

        UserResponseDto.Ranking userRanking = null;

        // 1명이라도 0점이 아니라면
        if (!matrixCount.isEmpty()) {
            Long matrixNumber = (Long) matrixCount.get(0).get(1);  // 맨 처음 user의 칸 수
            for (Tuple info : matrixCount) {
                if (Objects.equals(info.get(1), matrixNumber)) {  // 전 유저와 칸수가 같다면 랭크 유지

                    // 유저 찾았으면 저장해둠
                    if (Objects.equals((String)info.get(0), user.getNickname())) {
                        userRanking = new UserResponseDto.Ranking(rank, (String) info.get(0),
                                (Long) info.get(1));
                    }

                    matrixRankings.add(new UserResponseDto.Ranking(rank, (String) info.get(0),
                            (Long) info.get(1)));
                    count += 1;
                    continue;
                }

                // 전 유저보다 칸 수가 작다면 앞에 있는 사람수 만큼이 자신 랭킹
                count += 1;
                rank = count;

                // 유저 찾았으면 저장해둠
                if (Objects.equals((String)info.get(0), user.getNickname())) {
                    userRanking = new UserResponseDto.Ranking(rank, (String) info.get(0),
                            (Long) info.get(1));
                }

                matrixRankings.add(new UserResponseDto.Ranking(rank, (String) info.get(0),
                        (Long) info.get(1)));
                matrixNumber = (Long) info.get(1);  // 칸 수 update!
            }
            rank = count+1;
            // 나머지 0점인 유저들 추가
            for (int i = count; i < member.size(); i++) {
                // 유저 찾았으면 저장해둠
                if (Objects.equals(member.get(i).getNickname(), user.getNickname())) {
                    userRanking = new UserResponseDto.Ranking(rank, user.getNickname(),
                            0L);
                }
                matrixRankings.add(new UserResponseDto.Ranking(rank, member.get(i).getNickname(), 0L));
            }
            // 맨 앞 유저 추가
            matrixRankings.add(0, userRanking);
        }

        // 전부다 0점이라면
        else {
            // 맨앞 유저 추가
            matrixRankings.add(new UserResponseDto.Ranking(rank, user.getNickname(), 0L));
            for (int i = count; i < member.size(); i++) {
                matrixRankings.add(new UserResponseDto.Ranking(rank, member.get(i).getNickname(), 0L));
            }
        }

        return matrixRankings;
    }

    /*영역 기준 랭킹 계산(랭킹탭)*/
    public List<UserResponseDto.Ranking> calculateUserAreaRank(List<UserResponseDto.Ranking> areaRankings, User user) {
        //내림차순 정렬
        areaRankings.sort((a, b) -> b.getScore().compareTo(a.getScore()));

        Long areaNumber = areaRankings.get(0).getScore();  // 맨 처음 user의 영역 수
        int rank = 1;
        int count = 0;

        int userRank = 0;
        Long userArea = 0L;

        for (int i=1; i<areaRankings.size(); i++){
            // 전 유저와 영역 수가 같다면 랭크 유지
            if (Objects.equals(areaRankings.get(i).getScore(), areaNumber)){

                // 유저 찾았으면 저장해둠
                if (Objects.equals(areaRankings.get(i).getNickname(), user.getNickname())) {
                    userRank = rank;
                    userArea = areaRankings.get(i).getScore();
                }

                areaRankings.get(i).setRank(rank);
                count += 1;
                continue;
            }
            // 전 유저보다 영역수 가 작다면 앞에 있는 사람수 만큼이 자신 랭킹
            count += 1;
            rank = count;

            // 유저 찾았으면 저장해둠
            if (Objects.equals(areaRankings.get(i).getNickname(), user.getNickname())) {
                userRank = rank;
                userArea = areaRankings.get(i).getScore();
            }

            areaRankings.get(i).setRank(rank);
            areaNumber = areaRankings.get(i).getScore();  // 영역 수 update!
        }

        // 맨앞 유저 추가
        areaRankings.set(0, new UserResponseDto.Ranking(userRank, user.getNickname(), userArea));

        return areaRankings;
    }

}
