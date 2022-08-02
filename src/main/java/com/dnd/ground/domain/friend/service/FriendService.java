package com.dnd.ground.domain.friend.service;

import com.dnd.ground.domain.friend.dto.FriendResponseDto;

/**
 * @description 친구와 관련된 서비스의 역할을 분리한 인터페이스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1. 친구 목록 조회 기능 구현
 *          - 2022.08.02 박찬호
 */

public interface FriendService {
    FriendResponseDto getFriends(Long user);
}
