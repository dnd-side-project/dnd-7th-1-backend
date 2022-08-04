package com.dnd.ground.domain.exerciseRecord.service;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.exerciseRecord.dto.EndRequestDto;
import com.dnd.ground.domain.exerciseRecord.dto.StartResponseDto;
import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.domain.user.service.UserService;
import lombok.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @description 운동 기록 서비스 클래스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-04 / 운동기록 시작, 끝 로직 함수 구현: 박세헌
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExerciseRecordServiceImpl implements ExerciseRecordService {

    private final ExerciseRecordRepository exerciseRecordRepository;
    private final UserRepository userRepository;

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
        // 중복 제거
        Set<MatrixSet> setMatrices = new HashSet<>();
        recordOfThisWeek.forEach(r -> r.getMatrices().forEach(m -> setMatrices.add(MatrixSet.builder().latitude(m.getLatitude()).longitude(m.getLongitude()).build())));
        return new StartResponseDto(exerciseRecord.getId(), setMatrices.size());
    }

    // 기록 끝
    // 거리, matrix 저장
    @Transactional
    public ResponseEntity<?> recordEnd(EndRequestDto endRequestDto) {
        ExerciseRecord exerciseRecord = exerciseRecordRepository.findById(endRequestDto.getRecordId()).orElseThrow(); // 예외 처리
        exerciseRecord.endedTime(LocalDateTime.now());
        exerciseRecord.addDistance(endRequestDto.getDistance());
        endRequestDto.getMatrices().forEach(m -> exerciseRecord.addMatrix(new Matrix(m.getLatitude(), m.getLongitude())));
        exerciseRecordRepository.save(exerciseRecord);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    // 중복 제거
    @Builder
    static public class MatrixSet {
        public Double latitude;
        public Double longitude;

        @Override
        public int hashCode() {
            return Objects.hash(latitude, longitude);
        }

        @Override
        public boolean equals(Object obj) {
            if (this.getClass() != obj.getClass()) return false;
            return (Objects.equals(((MatrixSet) obj).latitude, this.latitude)) &&
                    (Objects.equals(((MatrixSet) obj).longitude, this.longitude));
        }
    }
}
