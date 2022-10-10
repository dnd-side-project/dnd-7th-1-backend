package com.dnd.ground.domain.friend.controller;

import com.dnd.ground.domain.friend.dto.FriendRequestDto;
import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @description 친구와 관련된 컨트롤러의 역할을 분리한 인터페이스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1.친구 요청 API 구현
 *          2.요청에 대한 응답 API 구현
 *          3.친구 삭제 API 구현
 *          - 2022.10.10 박찬호
 */

public interface FriendController {
    ResponseEntity<FriendResponseDto> getFriends(@PathVariable("nickname") String nickname);
    ResponseEntity<Boolean> requestFriend(@RequestBody FriendRequestDto.Request request);
    ResponseEntity<FriendResponseDto.ResponseResult> responseFriend(@RequestBody FriendRequestDto.Response request);
    ResponseEntity<Boolean> deleteFriend(@RequestBody FriendRequestDto.Request request);
}
