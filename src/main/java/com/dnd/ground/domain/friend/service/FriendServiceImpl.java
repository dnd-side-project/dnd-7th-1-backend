package com.dnd.ground.domain.friend.service;

import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.domain.friend.Friend;
import com.dnd.ground.domain.friend.FriendStatus;
import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import com.dnd.ground.domain.friend.repository.FriendRepository;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.CNotFoundException;
import com.dnd.ground.global.exception.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @description 친구와 관련된 서비스 구현체
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1.친구 요청 API 구현
 *          2.요청에 대한 응답 API 구현
 *          3.친구 삭제 API 구현
 *          - 2022.10.10 박찬호
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
        User findUser = userRepository.findByNickname(nickname).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER));

        List<Friend> findFriends = friendRepository.findFriendsById(findUser);
        List<FriendResponseDto.FInfo> infos = new ArrayList<>();

        //친구 정보 모음
        for (Friend findFriend : findFriends) {
            if (findFriend.getUser() == findUser) {
                infos.add(FriendResponseDto.FInfo.of()
                        .nickname(findFriend.getFriend().getNickname())
                        .picturePath(findFriend.getFriend().getPicturePath())
                        .build());
            } else if (findFriend.getFriend() == findUser) {
                infos.add(FriendResponseDto.FInfo.of()
                        .nickname(findFriend.getUser().getNickname())
                        .picturePath(findFriend.getFriend().getPicturePath())
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
            } else if (findFriend.getFriend() == user) {
                friends.add(findFriend.getUser());
            }
        }

        return friends;
    }

    /**
     * 단건 친구 요청 *
     * 누가 요청을 보냈는지 알기 위해 다음과 같은 명확한 기준을 세운다.
     * 추후 친구 요청 수락을 위해 요청을 보내는 쪽이 user, 받는 쪽이 friend에 저장한다.
     * 단, 한 친구 관계는 DB에 1번만 저장하도록 한다.
     * @param userNickname
     * @param friendNickname
     * @return
     */
    @Transactional
    public Boolean requestFriend(String userNickname, String friendNickname) {
        User user = userRepository.findByNickname(userNickname).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER)
        );

        User friend = userRepository.findByNickname(friendNickname).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER)
        );

        if (friendRepository.findFriendInProgress(user, friend).isPresent() || userNickname.equals(friendNickname)) {
            return false;
        } {
            Friend friendRelation = new Friend(user, friend, FriendStatus.Wait);
            friendRepository.save(friendRelation);
            return true;
        }
    }

    /*친구 요청 수락, 거절 등에 대한 처리*/
    @Transactional
    public FriendResponseDto.ResponseResult responseFriend(String userNickname, String friendNickname, FriendStatus status) {
        User user = userRepository.findByNickname(userNickname).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER)
        );

        User friend = userRepository.findByNickname(friendNickname).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER)
        );

        Friend friendRelation = friendRepository.findRequestFriend(user, friend).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_FRIEND_REQUEST)
        );

        friendRelation.updateStatus(status);
        return new FriendResponseDto.ResponseResult(user.getNickname(), friend.getNickname(), friendRelation.getStatus());
    }

    /*친구 삭제*/
    @Transactional
    public Boolean deleteFriend(String userNickname, String friendNickname) {
        User user = userRepository.findByNickname(userNickname).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER)
        );

        User friend = userRepository.findByNickname(friendNickname).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER)
        );

        Optional<Friend> friendRelation = friendRepository.findFriendRelation(user, friend);

        if (friendRelation.isPresent()) {
            friendRepository.delete(friendRelation.get());
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public Boolean requestFriends(User user, List<User> friends) {
        List<Friend> friendList = new ArrayList<>();

        for (User friend : friends) {
            friendList.add(
                    Friend.builder()
                            .user(user)
                            .friend(friend)
                            .status(FriendStatus.Wait)
                            .build()
            );
        }
        return null;
    }



    /* 수정 필요
    //챌린지를 진행하는 친구 조회
    public List<User> getChallenge(User user) {
        //친구 조회
        List<User> friends = getFriends(user);

        //챌린지가 없는 친구 삭제
        for (int i = 0 ; i<friends.size() ; i++) {
            User friend = friends.get(i);
            if (friend.getChallenges().isEmpty() || userChallengeRepository.findNotChallenging(friend).isPresent()) {
                friends.remove(friend);
                i--;
            }
        }

        return friends;
    }

    //챌린지를 진행하지 않는 친구 조회
    public List<User> getNotChallenge(User user) {
        //친구 조회
        List<User> friends = getFriends(user);

        //챌린지가 있는 친구 삭제
        for (int i = 0 ; i<friends.size() ; i++) {
            User friend = friends.get(i);
            if (friend.getChallenges().isEmpty() || userChallengeRepository.findChallenging(friend).isPresent()) {
                friends.remove(friend);
                i--;
            }
        }

        return friends;
    }
     */
}
