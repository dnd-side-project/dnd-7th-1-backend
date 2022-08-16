package com.dnd.ground.domain.exerciseRecord.service;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.exerciseRecord.dto.EndRequestDto;
import com.dnd.ground.domain.exerciseRecord.dto.StartResponseDto;
import com.dnd.ground.domain.friend.service.FriendService;
import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.matrix.dto.MatrixDto;
import com.dnd.ground.domain.matrix.matrixRepository.MatrixRepository;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.RankResponseDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import com.dnd.ground.domain.user.repository.UserRepository;
import lombok.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description 운동 기록 서비스 클래스
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated 2022-08-13 / 운동기록 추가 - 박세헌
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExerciseRecordServiceImpl implements ExerciseRecordService {

    private final ExerciseRecordRepository exerciseRecordRepository;
    private final UserRepository userRepository;
    private final MatrixRepository matrixRepository;
    private final FriendService friendService;

    @Transactional
    public void delete(Long exerciseRecordId) {
        exerciseRecordRepository.deleteById(exerciseRecordId);
    }

    // 기록 시작
    // 운동기록 id, 일주일 누적 영역 반환
    @Transactional
    public StartResponseDto recordStart(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow();  // 예외 처리
        ExerciseRecord exerciseRecord = new ExerciseRecord(user, LocalDateTime.now());
        exerciseRecordRepository.save(exerciseRecord);
        List<ExerciseRecord> recordOfThisWeek = exerciseRecordRepository.findRecordOfThisWeek(user.getId());
        if (recordOfThisWeek.isEmpty()) {
            return new StartResponseDto(exerciseRecord.getId(), 0);
        }

        return new StartResponseDto(exerciseRecord.getId(),
                matrixRepository.findMatrixSetByRecords(recordOfThisWeek).size());
    }

    // 기록 끝
    @Transactional
    public ResponseEntity<?> recordEnd(EndRequestDto endRequestDto) {
        //기록 조회
        ExerciseRecord exerciseRecord = exerciseRecordRepository.findById(endRequestDto.getRecordId()).orElseThrow(); // 예외 처리

        // 정보 update(ended, 거리, 걸음수, 운동시간)
        exerciseRecord.updateInfo(endRequestDto.getDistance(), endRequestDto.getStepCount(),
                endRequestDto.getExerciseTime(), endRequestDto.getMessage());

        //영역 저장
        List<MatrixDto> matrices = endRequestDto.getMatrices();
        matrices.forEach(m -> exerciseRecord.addMatrix(new Matrix(m.getLatitude(), m.getLongitude())));

        //회원 마지막 위치 최신화
        MatrixDto lastPosition = matrices.get(matrices.size() - 1);
        exerciseRecord.getUser().updatePosition(lastPosition.getLatitude(), lastPosition.getLongitude());

        exerciseRecordRepository.save(exerciseRecord);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    // 랭킹 조회(누적 걸음 수 기준)  (추후 파라미터 Requestdto로 교체 예정)
    public RankResponseDto.Step stepRanking(String nickname, LocalDateTime start, LocalDateTime end) {
        User user = userRepository.findByNickname(nickname).orElseThrow(); // 예외 처리
        List<User> userAndFriends = friendService.getFriends(user);  // 친구들 조회
        userAndFriends.add(0, user);  // 유저 추가
        List<UserResponseDto.Ranking> stepRankings = new ArrayList<>(); // [랭킹, 닉네임, 걸음 수]

        // [Tuple(닉네임, 걸음 수)] 걸음 수 기준 내림차순 정렬
        List<Tuple> stepCount = exerciseRecordRepository.findStepCount(userAndFriends, start, end);

        int count = 0;
        int rank = 1;

        // 1명이라도 0점이 아니라면
        if (!stepCount.isEmpty()) {
            Long matrixNumber = (Long) stepCount.get(0).get(1);  // 맨 처음 user의 걸음 수
            for (Tuple info : stepCount) {
                if (Objects.equals((Long) info.get(1), matrixNumber)) {  // 전 유저와 걸음 수가 같다면 랭크 유지
                    stepRankings.add(new UserResponseDto.Ranking(rank, (String) info.get(0),
                            (Long) info.get(1)));
                    count += 1;
                    continue;
                }
                // 전 유저보다 걸음수가 작다면 랭크+1
                rank += 1;
                stepRankings.add(new UserResponseDto.Ranking(rank, (String) info.get(0),
                        (Long) info.get(1)));
                matrixNumber = (Long) info.get(1);  // 걸음 수 update!
                count += 1;
            }
            rank += 1;
            // 나머지 0점인 유저들 추가
            for (int i = count; i < userAndFriends.size(); i++) {
                stepRankings.add(new UserResponseDto.Ranking(rank, userAndFriends.get(i).getNickname(), 0L));
            }
            return new RankResponseDto.Step(stepRankings);
        }

        // 전부다 0점이라면
        else {
            for (int i = count; i < userAndFriends.size(); i++) {
                stepRankings.add(new UserResponseDto.Ranking(rank, userAndFriends.get(i).getNickname(), 0L));
            }
            return new RankResponseDto.Step(stepRankings);
        }
    }
}
