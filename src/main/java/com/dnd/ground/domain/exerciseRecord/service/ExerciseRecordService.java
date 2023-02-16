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
 * @updated 2022-08-26 / 미사용 메소드 삭제 - 박찬호
 */

public interface ExerciseRecordService {

    ResponseEntity<Boolean> recordEnd(EndRequestDto endRequestDto);

    RankResponseDto.Step stepRanking(UserRequestDto.LookUp requestDto) ;

}
