package com.dnd.ground.domain.friend.controller;

import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @description 친구와 관련된 컨트롤러의 역할을 분리한 인터페이스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1. 친구 목록 조회 역할 추가
 *          - 2022.08.02 박찬호
 */

public interface FriendController {
    ResponseEntity<FriendResponseDto> getFriends(@PathVariable("nickname") String nickname);
}
