package com.dnd.ground.global.dummy;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @description 더미 데이터 생성을 위한 컨트롤러
 * @author  박찬호
 * @since   2022-10-04
 * @updated 1. 회원, 운동 기록, 영역과 관련된 로직 생성
 *          - 2022.10.04 박찬호
 */

@Api(tags = "더미 데이터")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/dummy")
@RestController
public class DummyController {

    private final DummyService dummyService;

    @GetMapping("/user")
    @Operation(summary = "회원 조회", description = "회원 정보를 반환함.")
    public ResponseEntity<?> getDummyUser(@RequestParam("nickname") String nickname) {
        return dummyService.getDummyUser(nickname);
    }

    @PostMapping("/create/user")
    @Operation(summary = "더미 회원 생성", description = "닉네임, 메일, 소개 메시지를 받아서 회원 생성\nResponse Header에 자체 엑세스, 리프레시 토큰을 담아준다.\n카카오와 관련된 서비스 사용X, 로그인X\n닉네임, 이메일이 중복되면 500 Status, 메일 형식 안맞춰도 에러난다.")
    public ResponseEntity<?> createDummyUser(@RequestBody DummyRequestDto.DummyUser request) {
        return dummyService.createDummyUser(request);
    }

    @PostMapping("/delete/user")
    @Operation(summary = "더미 회원 삭제", description = "닉네임을 받아서 회원 삭제\nDB에서 관련된 데이터(운동기록, 챌린지 등)를 싹 지움.")
    public ResponseEntity<?> deleteDummyUser(@RequestParam("nickname") String nickname) {
        return dummyService.deleteDummyUser(nickname);
    }

    @GetMapping("/record")
    @Operation(summary = "운동 기록 리스트 조회", description = "닉네임으로 해당 회원의 운동 기록 리스트 조회")
    public ResponseEntity<?> getDummyRecords(@RequestParam("nickname") String nickname) {
        return dummyService.getDummyRecords(nickname);
    }

    @PostMapping("/create/record")
    @Operation(summary = "운동 기록 생성", description = "운동 기록 생성, 운동기록 ID를 반환함.")
    public ResponseEntity<?> createDummyRecord(@RequestBody DummyRequestDto.DummyRecord request) {
        return dummyService.createDummyRecord(request);
    }

    @PostMapping("/insert/matrix")
    @Operation(summary = "운동 기록에 영역 추가", description = "추가된 영역 사이즈를 반환함.")
    public ResponseEntity<?> insertDummyMatrix(@RequestBody DummyRequestDto.DummyRecordMatrix request) {
        return dummyService.insertDummyMatrix(request);
    }

    @GetMapping("/matrix")
    @Operation(summary = "영역 조회", description = "해당 운동 기록에 저장된 영역 조회")
    public ResponseEntity<?> getDummyMatrices(@RequestParam("recordId") Long recordId) {
        return dummyService.getDummyMatrices(recordId);
    }

    @PostMapping("/delete/record")
    @Operation(summary = "운동 기록 삭제", description = "운동 기록에 포함된 영역도 함께 삭제해버림.")
    public ResponseEntity<?> deleteDummyRecord(@RequestParam("recordId") Long recordId) {
        return dummyService.deleteDummyRecord(recordId);
    }
}