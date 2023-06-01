package com.dnd.ground.domain.friend.controller;

import com.dnd.ground.domain.friend.dto.FriendRecommendRequestDto;
import com.dnd.ground.domain.friend.dto.FriendRequestDto;
import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import com.dnd.ground.domain.friend.service.FriendService;
import com.dnd.ground.global.exception.CommonException;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @description 친구와 관련된 컨트롤러 구현체
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1.친구 삭제 벌크 API 구현
 *          - 2023.05.17 박찬호
 */

@Api(tags = "친구")
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/friend")
@RestController
public class FriendControllerImpl implements FriendController {

    private final FriendService friendService;

    @GetMapping("/list")
    @Operation(summary = "친구 목록 조회", description = "닉네임을 통해 수락 상태의 친구 조회\n서버에서 15명씩 결과를 내려주고, 결과값의 isLast=false이면 경우 뒤에 친구가 더 있다는 뜻이므로, offset+1로 요청하면 됨.")
    public ResponseEntity<FriendResponseDto> getFriends(@Valid @ModelAttribute FriendRequestDto.FriendList request) {
        return ResponseEntity.ok(friendService.getFriends(request.getNickname(), request.getOffset(), request.getSize()));
    }

    @GetMapping("/receive")
    @Operation(summary = "친구 요청 목록 조회", description = "요청 대기 상태의 친구 목록 조회\n서버에서 3명씩 결과를 내려주고, 결과값의 isLast=false이면 경우 뒤에 친구가 더 있다는 뜻이므로, offset+1로 요청하면 됨.")
    public ResponseEntity<FriendResponseDto> getReceiveRequest(@Valid @ModelAttribute FriendRequestDto.FriendList request) {
        return ResponseEntity.ok(friendService.getReceiveRequest(request.getNickname(), request.getOffset(), request.getSize()));
    }

    @PostMapping("/request")
    @Operation(summary = "친구 요청하기", description = "요청하는 사람:user\n요청 받는 사람:friend\n결과가 false면, 친구 요청 중이거나 친구인 상태 or 같은 닉네임이 들어왔을 때.")
    public ResponseEntity<Boolean> requestFriend(@RequestBody FriendRequestDto.Request request) {
        return ResponseEntity.ok(friendService.requestFriend(request.getUserNickname(), request.getFriendNickname()));
    }

    @PostMapping("/response")
    @Operation(summary = "친구 상태 업데이트(수락, 거절 등)", description = "친구 요청에 대한 응답을 하는 API\n친구 요청을 수락하는 당사자가 user, 상대방이 friend, status는 상황에 맞게 다음과 같이 주면 된다.\n수락=Accept\n거절=Reject\n다른게 넘어오면 400 Status")
    public ResponseEntity<FriendResponseDto.ResponseResult> responseFriend(@RequestBody FriendRequestDto.Response request) {
        return ResponseEntity.ok(friendService.responseFriend(request.getUserNickname(), request.getFriendNickname(), request.getStatus()));
    }

    @PostMapping("/delete")
    @Operation(summary = "친구 삭제(친구 삭제 및 요청 삭제)", description = "DB에 있는 친구 관계 튜플을 삭제하는 것.\n친구 요청 상태(수락,거절,대기)와 상관 없이 아예 삭제함.\n삭제 성공 시 true 반환, 조회되는 친구 튜플이 없으면 false 반환")
    public ResponseEntity<Boolean> deleteFriend(@RequestBody FriendRequestDto.Request request) {
        return ResponseEntity.ok(friendService.deleteFriend(request.getUserNickname(), request.getFriendNickname()));
    }

    @PostMapping("/delete/bulk")
    @Operation(summary = "친구 목록 삭제(여러 개)", description = "한 명이라도 삭제가 되지 않으면, 삭제가 되지 않고 예외 처리")
    public ResponseEntity<Boolean> deleteFriends(@RequestBody FriendRequestDto.Bulk request) {
        return ResponseEntity.ok(friendService.deleteFriends(request.getNickname(), request.getFriends()));
    }

    @GetMapping("/recommend")
    @Operation(summary = "네모두 추천 친구", description = "네모두 추천 친구 API\n파라미터로 넘어온 Location을 기준으로 가까운 회원을 추천함.")
    public ResponseEntity<FriendResponseDto.RecommendResponse> recommendNemoduFriends(@ModelAttribute FriendRecommendRequestDto request) {
        return ResponseEntity.ok().body(friendService.recommendNemoduFriends(request.getNickname(), request.getLocation(), request.getDistance(), request.getSize()));
    }

    @GetMapping("/search")
    @Operation(summary = "친구 검색", description = "현재 ACCEPT 상태인 친구를 검색합니다.\n*두 글자 이상 검색 가능*")
    public ResponseEntity<List<FriendResponseDto.FInfo>> searchFriend(@RequestParam("nickname") String nickname,
                                                                      @RequestParam("keyword") String keyword) {
        if (keyword.length() < 2) throw new CommonException(ExceptionCodeSet.SEARCH_KEYWORD_INVALID);
        return ResponseEntity.ok().body(friendService.searchFriend(nickname, keyword));
    }
}
