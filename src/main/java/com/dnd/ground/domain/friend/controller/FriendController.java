package com.dnd.ground.domain.friend.controller;

import com.dnd.ground.domain.friend.dto.FriendRecommendRequestDto;
import com.dnd.ground.domain.friend.dto.FriendRequestDto;
import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @description 친구와 관련된 컨트롤러의 역할을 분리한 인터페이스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1.친구 삭제 벌크 API 구현
 *          - 2023.05.17 박찬호
 */

public interface FriendController {
    ResponseEntity<FriendResponseDto> getFriends(@ModelAttribute FriendRequestDto.FriendList request);
    ResponseEntity<FriendResponseDto> getReceiveRequest(@ModelAttribute FriendRequestDto.FriendList request);
    ResponseEntity<Boolean> requestFriend(@RequestBody FriendRequestDto.Request request);
    ResponseEntity<FriendResponseDto.ResponseResult> responseFriend(@RequestBody FriendRequestDto.Response request);
    ResponseEntity<Boolean> deleteFriend(@RequestBody FriendRequestDto.Request request);
    ResponseEntity<Boolean> deleteFriends(@RequestBody FriendRequestDto.Bulk request);
    ResponseEntity<FriendResponseDto.RecommendResponse> recommendNemoduFriends(@ModelAttribute FriendRecommendRequestDto request);
    ResponseEntity<List<FriendResponseDto.FInfo>> searchFriend(@RequestParam("nickname") String nickname, @RequestParam("keyword") String keyword);
}
