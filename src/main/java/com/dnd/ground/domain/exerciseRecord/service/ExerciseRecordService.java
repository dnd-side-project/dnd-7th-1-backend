package com.dnd.ground.domain.exerciseRecord.service;

import com.dnd.ground.domain.exerciseRecord.dto.EndRequestDto;
import org.springframework.http.ResponseEntity;


/**
 * @description 운동 기록 서비스 인터페이스
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated 1.걸음수 랭킹 API 리팩토링 및 위치 변경
 *          2023-02-22 박찬호
 */

public interface ExerciseRecordService {

    ResponseEntity<Boolean> recordEnd(EndRequestDto endRequestDto);
}
