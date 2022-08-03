package com.dnd.ground.domain.exerciseRecord.service;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.user.service.UserService;
import lombok.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * @description 운동 기록 서비스 클래스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-03 / 운동기록로직 삭제: 박세헌
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExerciseRecordServiceImpl implements ExerciseRecordService{

    private final ExerciseRecordRepository exerciseRecordRepository;

    @Transactional
    public ExerciseRecord save(ExerciseRecord exerciseRecord) {
        return exerciseRecordRepository.save(exerciseRecord);
    }

    public ExerciseRecord findById(Long exerciseId){
        return exerciseRecordRepository.findById(exerciseId).orElse(null);
    }

    @Transactional
    public void delete(Long exerciseRecordId){
        exerciseRecordRepository.deleteById(exerciseRecordId);
    }
}
