package com.dnd.ground.domain.exerciseRecord.service;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.exerciseRecord.dto.EndRequestDto;
import com.dnd.ground.domain.friend.service.FriendService;
import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.RankResponseDto;
import com.dnd.ground.domain.user.dto.UserRequestDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.exception.UserException;
import lombok.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @description 운동 기록 서비스 클래스
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated 1.기록 시작 API 삭제
 *          - 2023-02-16
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExerciseRecordServiceImpl implements ExerciseRecordService {

    private final ExerciseRecordRepository exerciseRecordRepository;
    private final UserRepository userRepository;
    private final FriendService friendService;

    // 기록 끝
    @Transactional
    public ResponseEntity<Boolean> recordEnd(EndRequestDto endRequestDto) {
        // 유저 찾아서 운동 기록 생성
        User user = userRepository.findByNickname(endRequestDto.getNickname()).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));
        ExerciseRecord exerciseRecord = new ExerciseRecord(user);

        // 정보 update(ended, 거리, 걸음수, 운동시간, 상세 기록, 시작 시간, 끝 시간)
        exerciseRecord.updateInfo(endRequestDto.getDistance(), endRequestDto.getStepCount(),
                endRequestDto.getExerciseTime(), endRequestDto.getMessage(), endRequestDto.getStarted(), endRequestDto.getEnded());

        //영역 저장
        ArrayList<ArrayList<Double>> matrices = endRequestDto.getMatrices();
        for (ArrayList<Double> matrix : matrices) {
            exerciseRecord.addMatrix(new Matrix(matrix.get(0), matrix.get(1)));
        }

        //회원 마지막 위치 최신화
        ArrayList<Double> lastPosition = matrices.get(matrices.size() - 1);
        exerciseRecord.getUser().updatePosition(lastPosition.get(0), lastPosition.get(1));

        exerciseRecordRepository.save(exerciseRecord);
        return new ResponseEntity(true, HttpStatus.CREATED);
    }

    // 랭킹 조회(누적 걸음 수 기준)  (추후 파라미터 Requestdto로 교체 예정)
    public RankResponseDto.Step stepRanking(UserRequestDto.LookUp requestDto) {

        String nickname = requestDto.getNickname();
        LocalDateTime start = requestDto.getStart();
        LocalDateTime end = requestDto.getEnd();

        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        List<User> userAndFriends = friendService.getFriends(user);  // 친구들 조회
        userAndFriends.add(0, user);  // 유저 추가
        List<UserResponseDto.Ranking> stepRankings = new ArrayList<>(); // [랭킹, 닉네임, 걸음 수]

        // [Tuple(닉네임, 걸음 수, 프로필 path)] 걸음 수 기준 내림차순 정렬
        List<Tuple> stepCount = exerciseRecordRepository.findStepCount(userAndFriends, start, end);

        if (stepCount.isEmpty()){
            for (User users : userAndFriends) {
                stepRankings.add(new UserResponseDto.Ranking(1, (String) users.getNickname(),
                        0L, users.getPicturePath()));
            }
            return new RankResponseDto.Step(stepRankings);
        }

        int count = 0;
        int rank = 1;

        Long matrixNumber = (Long) stepCount.get(0).get(1);  // 맨 처음 user의 걸음 수
        for (Tuple info : stepCount) {
            // 전 유저와 걸음 수가 같다면 랭크 유지
            if (Objects.equals(info.get(1), matrixNumber)) {
                stepRankings.add(new UserResponseDto.Ranking(rank, (String) info.get(0),
                        (Long) info.get(1), (String) info.get(2)));
                count += 1;
                continue;
            }

            // 전 유저보다 걸음수가 작다면 앞에 있는 사람수 만큼이 자신 랭킹
            count += 1;
            rank = count;
            stepRankings.add(new UserResponseDto.Ranking(rank, (String) info.get(0),
                    (Long) info.get(1), (String) info.get(2)));
            matrixNumber = (Long) info.get(1);  // 걸음 수 update!
        }
        return new RankResponseDto.Step(stepRankings);
    }
}
