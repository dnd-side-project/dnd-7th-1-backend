package com.dnd.ground.domain.exerciseRecord.service;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.dto.EndRequestDto;
import com.dnd.ground.domain.exerciseRecord.dto.StartResponseDto;
import com.dnd.ground.domain.user.dto.RankResponseDto;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 운동 기록 서비스 인터페이스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-12 / 걸음 수 랭킹 조회 함수 - 박세헌
 */

public interface ExerciseRecordService {

    void delete(Long exerciseRecordId);

    StartResponseDto recordStart(String nickname);

    ResponseEntity<?> recordEnd(EndRequestDto endRequestDto);

    RankResponseDto.Step stepRanking(String nickname, LocalDateTime start, LocalDateTime end);

}
