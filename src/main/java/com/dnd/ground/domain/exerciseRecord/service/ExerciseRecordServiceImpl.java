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
 * @updated 2022-08-01 / 생성 및 함수 추가: 박세헌
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

    // 개인 이번주 기록
    // start: 이번 주 월요일 00:00:00
    // end: 지금
    public List<ExerciseRecord> findRecordOfThisWeek(Long id) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime result = end.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime start = LocalDateTime.of(result.getYear(), result.getMonth(), result.getDayOfMonth(), 0, 0, 0);
        System.out.println("시작: " + start + " 끝: " + end);
        return exerciseRecordRepository.findRecord(id, start, end);
    }

    // 개인 과거 기록(일)
    // start: 해당 일 00:00:00 (request 받음)
    // end: 해당 일 23:59:59
    public List<ExerciseRecord> findRecordOfPastByDay(Long id, LocalDateTime start){
        LocalDateTime end = LocalDateTime.of(start.getYear(), start.getMonth(), start.getDayOfMonth(), 23, 59, 59);
        return exerciseRecordRepository.findRecord(id, start, end);
    }

    // 개인 과거 기록(주)
    // start: 해당 주의 월요일 00:00:00 (request 받음)
    // end: 해당 주의 일요일 23:59:59
    public List<ExerciseRecord> findRecordOfPastByWeek(Long id, LocalDateTime start){
        LocalDateTime result = LocalDateTime.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDateTime end = LocalDateTime.of(result.getYear(), result.getMonth(), result.getDayOfMonth(), 23, 59, 59);
        return exerciseRecordRepository.findRecord(id, start, end);
    }

    // 챌린지 이번주 기록
    // start: 챌린지 시작 시간 (request 받음)
    // end: 지금
    public List<ExerciseRecord> findChallengeRecordOfThisWeek(Long id, LocalDateTime start) {
        return exerciseRecordRepository.findRecord(id, start, LocalDateTime.now());
    }

    // 챌린지 과거 기록
    // start: 챌린지 시작 시간 (request 받음)
    // end: 해당 주의 일요일 23:59:59
    public List<ExerciseRecord> findChallengeRecordOfPast(Long id, LocalDateTime start) {
        LocalDateTime result = LocalDateTime.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDateTime end = LocalDateTime.of(result.getYear(), result.getMonth(), result.getDayOfMonth(), 23, 59, 59);
        return exerciseRecordRepository.findRecord(id, start, end);
    }
}
