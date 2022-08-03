package com.dnd.ground.domain.exerciseRecord.controller;

import com.dnd.ground.domain.exerciseRecord.dto.EndRequestDto;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @description 기록 컨트롤러 클래스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-02 / 생성 : 박세헌
 */

@Api(tags = "운동기록")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/record")
@RestController
public class RecordControllerImpl implements RecordController{

    @PostMapping("/start")
    public ResponseEntity<?> start(@RequestParam("nickname") String nickname){
        // 유저의 운동기록 생성(started=now())
        // 운동기록 id와 (일주일)누적기록 response (StartResponseDto)
        return ResponseEntity.ok("기록 시작");
    }

    @PostMapping("/end")
    public ResponseEntity<?> end(@RequestBody EndRequestDto endRequestDto){
        // 운동기록Id, 거리, matrix 받아옴 (EndRequestDto)
        // 운동기록 찾아서 거리, matrix 저장
        // ended = now()
        return ResponseEntity.ok("기록 끝");
    }
}
