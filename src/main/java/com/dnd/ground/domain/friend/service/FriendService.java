package com.dnd.ground.domain.friend.service;

import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import com.dnd.ground.domain.user.User;

import java.util.List;

/**
 * @description 친구와 관련된 서비스의 역할을 분리한 인터페이스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1. 챌린지 진행중인 친구 목록 조회
 *          2. 챌린지를 진행중이지 않은 친구 목록 조회
 *          - 2022.08.05 박찬호
 */

public interface FriendService {
    FriendResponseDto getFriends(String nickname); //친구 목록 조회용 DTO
    List<User> getFriends(User user); // 친구 목록 조회

//    List<User> getChallenge(User user); // 챌린지 진행중인 친구 목록 조회
//    List<User> getNotChallenge(User user); //챌린지를 진행중이지 않은 친구 목록 조회
}
