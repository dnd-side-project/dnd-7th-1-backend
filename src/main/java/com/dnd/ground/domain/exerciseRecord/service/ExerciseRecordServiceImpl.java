package com.dnd.ground.domain.exerciseRecord.service;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.exerciseRecord.dto.EndRequestDto;
import com.dnd.ground.domain.exerciseRecord.dto.StartResponseDto;
import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.matrix.dto.MatrixDto;
import com.dnd.ground.domain.matrix.matrixRepository.MatrixRepository;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.repository.UserRepository;
import lombok.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 운동 기록 서비스 클래스
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated recordEnd 메소드 변경
 *          1. 기록 종료 시, 회원의 마지막 위치 최신화
 *          - 2022.08.09 박찬호
 *          1. 거리, 걸음수, 운동시간 추가
 *          - 2022.08.12 박세헌
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExerciseRecordServiceImpl implements ExerciseRecordService {

    private final ExerciseRecordRepository exerciseRecordRepository;
    private final UserRepository userRepository;
    private final MatrixRepository matrixRepository;

    @Transactional
    public void delete(Long exerciseRecordId) {
        exerciseRecordRepository.deleteById(exerciseRecordId);
    }

    // 기록 시작
    // 운동기록 id, 일주일 누적 영역 반환
    @Transactional
    public StartResponseDto recordStart(String nickname) {
        User user = userRepository.findByNickName(nickname).orElseThrow();  // 예외 처리
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
        exerciseRecord.updateInfo(endRequestDto.getDistance(), endRequestDto.getStepCount(), endRequestDto.getMinute(), endRequestDto.getSecond());

        //영역 저장
        List<MatrixDto> matrices = endRequestDto.getMatrices();
        matrices.forEach(m -> exerciseRecord.addMatrix(new Matrix(m.getLatitude(), m.getLongitude())));

        //회원 마지막 위치 최신화
        MatrixDto lastPosition = matrices.get(matrices.size() - 1);
        exerciseRecord.getUser().updatePosition(lastPosition.getLatitude(), lastPosition.getLongitude());

        exerciseRecordRepository.save(exerciseRecord);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
