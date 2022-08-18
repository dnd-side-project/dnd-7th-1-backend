package com.dnd.ground.domain.friend.controller;

import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import com.dnd.ground.domain.friend.service.FriendService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @description 친구와 관련된 컨트롤러 구현체
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1. API 명세 수정
 *          - 2022.08.18 박찬호
 */

@Api(tags = "친구")
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/friend")
@RestController
public class FriendControllerImpl implements FriendController {

    private final FriendService friendService;

    @GetMapping("/list/{nickname}")
    @Operation(summary = "친구 목록 조회", description = "닉네임을 통해 수락 상태의 친구 조회")
    public ResponseEntity<FriendResponseDto> getFriends(@PathVariable("nickname") String nickname) {
        return ResponseEntity.ok(friendService.getFriends(nickname));
    }

}
