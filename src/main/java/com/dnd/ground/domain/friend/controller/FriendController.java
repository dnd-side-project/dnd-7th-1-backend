package com.dnd.ground.domain.friend.controller;

import com.dnd.ground.domain.friend.dto.FriendRequestDto;
import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @description 친구와 관련된 컨트롤러의 역할을 분리한 인터페이스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1.친구 목록 조회 페이징 적용
 *          2.친구 요청 목록 조회 기능 구현
 *          - 2022.10.29 박찬호
 */

public interface FriendController {
    ResponseEntity<FriendResponseDto> getFriends(@RequestParam("nickname") String nickname,
                                                 @RequestParam("offset") Integer offset);
    ResponseEntity<FriendResponseDto.ReceiveRequest> getReceiveRequest(@RequestParam("nickname") String nickname, @RequestParam("offset") Integer offset);
    ResponseEntity<Boolean> requestFriend(@RequestBody FriendRequestDto.Request request);
    ResponseEntity<FriendResponseDto.ResponseResult> responseFriend(@RequestBody FriendRequestDto.Response request);
    ResponseEntity<Boolean> deleteFriend(@RequestBody FriendRequestDto.Request request);
}
