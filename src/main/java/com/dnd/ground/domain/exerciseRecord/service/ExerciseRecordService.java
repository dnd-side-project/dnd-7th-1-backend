package com.dnd.ground.domain.exerciseRecord.service;

import com.dnd.ground.domain.exerciseRecord.dto.RecordCreateDto;


/**
 * @description 운동 기록 서비스 인터페이스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1.메소드 이름 변경
 *          2023-03-05 박찬호
 */

public interface ExerciseRecordService {

    Boolean createExerciseRecord(RecordCreateDto endRequestDto);
}
