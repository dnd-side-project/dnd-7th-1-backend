package com.dnd.ground.domain.friend.service;

import com.dnd.ground.domain.friend.Friend;
import com.dnd.ground.domain.friend.FriendStatus;
import com.dnd.ground.domain.friend.dto.FriendCondition;
import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import com.dnd.ground.domain.friend.repository.FriendRepository;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.exception.FriendException;
import com.dnd.ground.global.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.dnd.ground.domain.friend.FriendStatus.*;

/**
 * @description 친구와 관련된 서비스 구현체
 * @author 박찬호
 * @since 2022-08-01
 * @updated 1. 요청을 보낸 경우 user에 요청한 사람, friend에 받은 사람을 WAIT 상태로 저장.
 *          2. 친구 요청 수락한 경우, ACCEPT 상태의 친구 데이터를 2개 저장.
 *          3. 이에 따른 메소드 수정
 *         2023-03-06
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    private static final Integer FRIEND_LARGE_PAGING_NUMBER = 15; //15개씩 페이징하기 위해 1개 더 가져옴(마지막 여부 판단)
    private static final Integer FRIEND_SMALL_PAGING_NUMBER = 3; //3개씩 페이징 하기 위해 1개 더 가져옴.

    //친구 목록과 추가 정보를 함께 반환
    public FriendResponseDto getFriends(String nickname, Integer offset) {

        //유저 및 친구 조회
        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        List<FriendResponseDto.FInfo> infos = new ArrayList<>();
        boolean isLast;

        PageRequest pageRequest = PageRequest.of(offset, FRIEND_LARGE_PAGING_NUMBER);

        try {
            Slice<Friend> findFriendSlice = friendRepository.findFriendsByUserWithPaging(user, pageRequest);
            isLast = findFriendSlice.isLast();

            List<Friend> findFriends = findFriendSlice.getContent();

            for (Friend findFriend : findFriends) {
                infos.add(FriendResponseDto.FInfo.of()
                        .nickname(findFriend.getFriend().getNickname())
                        .picturePath(findFriend.getFriend().getPicturePath())
                        .build());
            }
            return FriendResponseDto.builder()
                    .infos(infos)
                    .size(findFriends.size())
                    .isLast(isLast)
                    .build();

        } catch (NullPointerException e) {
            return FriendResponseDto.builder()
                    .infos(infos)
                    .size(0)
                    .isLast(true)
                    .build();
        }
    }

    //요청받은 친구 목록 조회
    public FriendResponseDto.ReceiveRequest getReceiveRequest(String nickname, Integer offset) {
        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        FriendResponseDto.ReceiveRequest response = new FriendResponseDto.ReceiveRequest();
        boolean isLast;

        PageRequest pageRequest = PageRequest.of(offset, FRIEND_SMALL_PAGING_NUMBER);
        Slice<User> receiveRequestSlice = friendRepository.findReceiveRequest(user, pageRequest);
        isLast = receiveRequestSlice.isLast();

        List<User> receiveRequest = receiveRequestSlice.getContent();

        for (User friend : receiveRequest) {
            response.getFriendsInfo().add(
                    FriendResponseDto.FInfo.of()
                            .nickname(friend.getNickname())
                            .picturePath(friend.getPicturePath())
                            .build()
            );
        }

        response.setSize(receiveRequest.size());
        response.setIsLast(isLast);
        return response;
    }

    //List<User> 형태의 친구 목록 반환
    public List<User> getFriends(User user) {
        FriendCondition condition = FriendCondition.builder()
                .user(user)
                .status(ACCEPT)
                .build();

        return friendRepository.findFriends(condition);
    }

    /**
     * 단건 친구 요청 *
     * 누가 요청을 보냈는지 알기 위해 다음과 같은 명확한 기준을 세운다.
     * 추후 친구 요청 수락을 위해 요청을 보내는 쪽이 user, 받는 쪽이 friend에 저장한다.
     * 요청한 경우 1건만 저장되지만, 수락하게 되면 양 측에 저장되어 2건이 저장된다.
     *
     * @param userNickname
     * @param friendNickname
     * @return boolean
     */
    @Transactional
    public Boolean requestFriend(String userNickname, String friendNickname) {
        User user = userRepository.findByNickname(userNickname)
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        User friend = userRepository.findByNickname(friendNickname)
                .orElseThrow(() -> new FriendException(ExceptionCodeSet.FRIEND_NOT_FOUND));

        if (friendRepository.findFriendInProgress(user, friend).isPresent())
            throw new FriendException(ExceptionCodeSet.FRIEND_DUPL);
        else if (userNickname.equals(friendNickname)) throw new FriendException(ExceptionCodeSet.BAD_REQUEST);

        friendRepository.save(new Friend(user, friend, WAIT));
        return true;
    }

    /*친구 요청 수락, 거절 등에 대한 처리*/
    @Transactional
    public FriendResponseDto.ResponseResult responseFriend(String userNickname, String friendNickname, FriendStatus status) {
        User user = userRepository.findByNickname(userNickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND)
        );

        User friend = userRepository.findByNickname(friendNickname).orElseThrow(
                () -> new FriendException(ExceptionCodeSet.FRIEND_NOT_FOUND)
        );

        Friend friendRelation = friendRepository.findRequestFriend(user, friend).orElseThrow(
                () -> new FriendException(ExceptionCodeSet.FRIEND_NOT_FOUND_REQ)
        );

        if (status.equals(ACCEPT)) {
            friendRelation.updateStatus(ACCEPT);
            friendRepository.save(
                    Friend.builder()
                            .user(friend)
                            .friend(user)
                            .status(ACCEPT)
                            .build()
            );
        } else if (status.equals(REJECT)) {
            friendRepository.delete(friendRelation);
        } else throw new FriendException(ExceptionCodeSet.FRIEND_INVALID_STATUS);

        return new FriendResponseDto.ResponseResult(user.getNickname(), friend.getNickname(), friendRelation.getStatus());
    }

    /*친구 삭제*/
    @Transactional
    public Boolean deleteFriend(String userNickname, String friendNickname) {
        User user = userRepository.findByNickname(userNickname)
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        User friend = userRepository.findByNickname(friendNickname)
                .orElseThrow(() -> new FriendException(ExceptionCodeSet.FRIEND_NOT_FOUND));

        List<Friend> friendRelation = friendRepository.findFriendRelation(user, friend);
        if (friendRelation.isEmpty()) throw new FriendException(ExceptionCodeSet.FRIEND_NOT_FOUND);
        else {
            friendRepository.deleteAll(friendRelation);
            return true;
        }
    }

    /*회원과 친구가 어떤 관계인지 나타내는 메소드*/
    public FriendStatus getFriendStatus(User user, User friend) {
        List<Friend> friendRelation = friendRepository.findFriendRelation(user, friend);
        FriendStatus isFriend = NO_FRIEND;

        if (friendRelation.size() == 2) {
            for (Friend f : friendRelation) {
                if (!f.getStatus().equals(ACCEPT))
                    return NO_FRIEND;
            }
            isFriend = ACCEPT;
        }
        else if (friendRelation.size() == 1) {
            Friend f = friendRelation.get(0);
            FriendStatus status = f.getStatus();
            if (status.equals(WAIT)) {
                if (f.getUser().equals(user)) isFriend = REQUESTING;
                else isFriend = RESPONSE_WAIT;
            }
        }

        return isFriend;
    }
}
