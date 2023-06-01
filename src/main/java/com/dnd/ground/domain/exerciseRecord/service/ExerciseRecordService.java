package com.dnd.ground.domain.exerciseRecord.service;

import com.dnd.ground.domain.exerciseRecord.dto.RecordCreateDto;
import com.dnd.ground.domain.user.User;


/**
 * @description 운동 기록 서비스 인터페이스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1. 회원 탈퇴 API 구현 - 운동 기록 및 영역 삭제
 *          - 2023.05.22 박찬호
 */

public interface ExerciseRecordService {

    Boolean createExerciseRecord(RecordCreateDto endRequestDto);
    void deleteRecordAll(User user);
}
