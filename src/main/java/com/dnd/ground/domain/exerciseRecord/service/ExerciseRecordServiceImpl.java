package com.dnd.ground.domain.exerciseRecord.service;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.exerciseRecord.dto.RecordCreateDto;
import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.matrix.dto.Location;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.exception.UserException;
import lombok.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @description 운동 기록 서비스 클래스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1.기록 저장 API 수정(반환 타입 수정 및 객체 생성 방식 변경)
 *          2.메소드 이름 변경 (end -> createExerciseRecord)
 *          2023-03-05 박찬호
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExerciseRecordServiceImpl implements ExerciseRecordService {

    private final ExerciseRecordRepository exerciseRecordRepository;
    private final UserRepository userRepository;

    // 운동 기록 생성
    @Transactional
    public Boolean createExerciseRecord(RecordCreateDto endRequestDto) {
        User user = userRepository.findByNickname(endRequestDto.getNickname())
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        ExerciseRecord exerciseRecord = ExerciseRecord.builder()
                .user(user)
                .started(endRequestDto.getStarted())
                .ended(endRequestDto.getEnded())
                .distance(endRequestDto.getDistance())
                .exerciseTime(endRequestDto.getExerciseTime())
                .stepCount(endRequestDto.getStepCount())
                .message(endRequestDto.getMessage())
                .build();

        //영역 저장
        ArrayList<Location> matrices = endRequestDto.getMatrices();
        for (Location matrix : matrices) {
            exerciseRecord.addMatrix(new Matrix(matrix.getLatitude(), matrix.getLongitude()));
        }

        exerciseRecordRepository.save(exerciseRecord);

        //회원 마지막 위치 최신화
        Location lastPosition = matrices.get(matrices.size() - 1);
        user.updatePosition(lastPosition.getLatitude(), lastPosition.getLongitude());

        return true;
    }
}
