package com.dnd.ground.domain.friend.service;

import com.dnd.ground.domain.friend.Friend;
import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import com.dnd.ground.domain.friend.repository.FriendRepository;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 친구와 관련된 서비스 구현체
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1. 친구 목록 조회 기능 구현
 *          - 2022.08.02 박찬호
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    //친구 목록과 추가 정보를 함께 반환
    @Transactional
    public FriendResponseDto getFriends(String nickname) {

        //유저 및 친구 조회
        User findUser = userRepository.findByNickName(nickname).orElseThrow(); //예외 처리 예정!
        List<Friend> findFriends = friendRepository.findFriendsById(findUser);
        List<FriendResponseDto.Info> infos = new ArrayList<>();

        //친구 정보 모음
        for (Friend findFriend : findFriends) {
            if (findFriend.getUser() == findUser) {
                infos.add(FriendResponseDto.Info.of()
                        .nickname(findFriend.getFriend().getNickName())
                        .build());
            }
            else if (findFriend.getFriend() == findUser) {
                infos.add(FriendResponseDto.Info.of()
                        .nickname(findFriend.getUser().getNickName())
                        .build());
            }
        }

        return FriendResponseDto.builder()
                .infos(infos)
                .size(findFriends.size())
                .build();
    }

    //List<User> 형태의 친구 목록 반환
    public List<User> getFriends(User user) {
        List<Friend> findFriends = friendRepository.findFriendsById(user);
        List<User> friends = new ArrayList<>();


        for (Friend findFriend : findFriends) {
            if (findFriend.getUser() == user) {
                friends.add(findFriend.getFriend());
            }
            else if (findFriend.getFriend() == user) {
                friends.add(findFriend.getUser());
            }
        }

        return friends;
    }
}
