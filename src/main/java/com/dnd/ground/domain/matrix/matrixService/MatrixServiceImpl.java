package com.dnd.ground.domain.matrix.matrixService;

import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.exerciseRecord.service.ExerciseRecordService;
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
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description 운동 영역 서비스 클래스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 1. 랭킹 관련 메소드 이동(UserService -> MatrixService)
 *          - 2022.08.11 박찬호
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatrixServiceImpl implements MatrixService {
    private final MatrixRepository matrixRepository;
    private final UserRepository userRepository;
    private final FriendService friendService;
    private final ExerciseRecordService exerciseRecordService;
    private final ExerciseRecordRepository exerciseRecordRepository;

    @Transactional
    public Matrix save(Matrix matrix){
        return matrixRepository.save(matrix);
    }


    // 랭킹 조회(누적 칸의 수 기준)
    public RankResponseDto.matrixRankingResponseDto matrixRanking(String nickname){
        User user = userRepository.findByNickName(nickname).orElseThrow();
        List<User> userAndFriends = friendService.getFriends(user);  // 친구들 조회
        userAndFriends.add(0, user);  // 유저 추가
        List<UserResponseDto.matrixRanking> matrixRankings = new ArrayList<>(); // [랭킹, 닉네임, 칸의 수]

        // start: 월요일, end: 지금
        LocalDateTime result = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime start = LocalDateTime.of(result.getYear(), result.getMonth(), result.getDayOfMonth(), 0, 0, 0);
        LocalDateTime end = LocalDateTime.now();

        // [Tuple(닉네임, 이번주 누적 칸수)] 칸수 기준 내림차순 정렬
        List<Tuple> matrixCount = userRepository.findMatrixCount(userAndFriends, start, end);

        int count = 0;
        int rank = 1;
        if (!matrixCount.isEmpty()){
            Long matrixNumber = (Long) matrixCount.get(0).get(1);  // 맨 처음 user의 칸 수
            for (Tuple info : matrixCount) {
                if (Objects.equals((Long) info.get(1), matrixNumber)){  // 전 유저와 칸수가 같다면 랭크 유지
                    matrixRankings.add(new UserResponseDto.matrixRanking(rank, (String)info.get(0),
                            (Long)info.get(1)));
                    count += 1;
                    continue;
                }
                // 전 유저보다 작다면 랭크+1
                rank += 1;
                matrixRankings.add(new UserResponseDto.matrixRanking(rank, (String)info.get(0),
                        (Long)info.get(1)));
                matrixNumber = (Long)info.get(1);  // 칸 수 update!
                count += 1;
            }
        }

        rank += 1;
        // 나머지 0점인 유저들 추가
        for (int i=count; i<userAndFriends.size(); i++){
            matrixRankings.add(new UserResponseDto.matrixRanking(rank, userAndFriends.get(i).getNickName(), 0L));
        }

        return new RankResponseDto.matrixRankingResponseDto(matrixRankings);
    }

    // 랭킹 조회(누적 영역의 수 기준)
    public RankResponseDto.areaRankingResponseDto areaRanking(String nickname) {
        User user = userRepository.findByNickName(nickname).orElseThrow();
        List<User> friends = friendService.getFriends(user);  // 친구들 조회
        List<UserResponseDto.areaRanking> areaRankings = new ArrayList<>();  // [랭킹, 닉네임, 영역의 수]

        // 유저의 닉네임과 (이번주)영역의 수 대입
        areaRankings.add(new UserResponseDto.areaRanking(1, user.getNickName(),
                exerciseRecordService.findAreaNumber(exerciseRecordRepository.findRecordOfThisWeek(user.getId()))));

        // 친구들의 닉네임과 (이번주)영역의 수 대입
        friends.forEach(f -> areaRankings.add(new UserResponseDto.areaRanking(1, f.getNickName(),
                exerciseRecordService.findAreaNumber(exerciseRecordRepository.findRecordOfThisWeek(f.getId())))));

        // 영역의 수를 기준으로 내림차순 정렬
        areaRankings.sort((a, b) -> b.getAreaNumber().compareTo(a.getAreaNumber()));

        // 랭크 결정
        Long areaNumber = areaRankings.get(0).getAreaNumber();  // 맨 처음 user의 영역 수
        int rank = 1;
        for (int i=1; i<areaRankings.size(); i++){
            if (Objects.equals(areaRankings.get(i).getAreaNumber(), areaNumber)){  // 전 유저와 칸수가 같다면 랭크 유지
                areaRankings.get(i).setRank(rank);
                continue;
            }
            // 전 유저보다 칸수가 작다면 랭크+1
            rank += 1;
            areaRankings.get(i).setRank(rank);
            areaNumber = areaRankings.get(i).getAreaNumber();  // 영역 수 update!
        }
        return new RankResponseDto.areaRankingResponseDto(areaRankings);
    }

}
