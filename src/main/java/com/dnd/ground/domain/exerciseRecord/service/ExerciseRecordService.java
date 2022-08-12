package com.dnd.ground.domain.exerciseRecord.service;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.dto.EndRequestDto;
import com.dnd.ground.domain.exerciseRecord.dto.StartResponseDto;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 운동 기록 서비스 인터페이스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-04 / 기록 시작, 기록 끝: 박세헌
 */

public interface ExerciseRecordService {

    void delete(Long exerciseRecordId);

    StartResponseDto recordStart(String nickname);

    ResponseEntity<?> recordEnd(EndRequestDto endRequestDto);

}
