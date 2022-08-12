package com.dnd.ground.domain.friend;

import com.dnd.ground.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FriendTest {

    @Test
    @Transactional
    public void 친구() {

        User userA = User.builder()
                .username("nameA")
                .nickname("nickA")
                .friends(new ArrayList<>())
                .challenges(new ArrayList<>())
                .build();

        User userC = User.builder()
                .username("nameC")
                .nickname("nickC")
                .friends(new ArrayList<>())
                .challenges(new ArrayList<>())
                .build();

        Friend friendA = new Friend(1L, userA, userC, null);
        Friend friendB = new Friend(2L, userC, userA, null);

        userA.getFriends().add(friendA);
        userC.getFriends().add(friendB);
    }

}