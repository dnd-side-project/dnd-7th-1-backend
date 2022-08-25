package com.dnd.ground.domain.exerciseRecord.service;

import com.dnd.ground.domain.exerciseRecord.dto.EndRequestDto;
import com.dnd.ground.domain.user.dto.HomeResponseDto;
import com.dnd.ground.domain.user.dto.RankResponseDto;
import com.dnd.ground.domain.user.dto.UserRequestDto;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

/**
 * @description 운동 기록 서비스 인터페이스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-26 / 컨트롤러-서비스단 전달 형태 변경 - 박세헌
 */

public interface ExerciseRecordService {

    void delete(Long exerciseRecordId);

    HomeResponseDto recordStart(String nickname);

    ResponseEntity<Boolean> recordEnd(EndRequestDto endRequestDto);

    RankResponseDto.Step stepRanking(UserRequestDto.LookUp requestDto) ;

}
