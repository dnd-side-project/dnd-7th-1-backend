package com.dnd.ground.domain.exerciseRecord.service;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.exerciseRecord.dto.EndRequestDto;
import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.exception.UserException;
import lombok.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @description 운동 기록 서비스 클래스
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated 1.걸음수 랭킹 API 리팩토링 및 위치 변경
 *          2023-02-22 박찬호
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExerciseRecordServiceImpl implements ExerciseRecordService {

    private final ExerciseRecordRepository exerciseRecordRepository;
    private final UserRepository userRepository;

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
}
