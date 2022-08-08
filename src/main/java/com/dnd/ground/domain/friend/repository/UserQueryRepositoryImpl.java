package com.dnd.ground.domain.friend.repository;


import com.dnd.ground.domain.friend.FriendStatus;
import com.dnd.ground.domain.friend.QFriend;
import com.dnd.ground.domain.user.QUser;
import com.dnd.ground.domain.user.User;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl {
    private final JPAQueryFactory query;

    QUser user = QUser.user;
    QFriend friend = QFriend.friend1;

    public Set<User> findFriends(User myUser){
        return query
                .select(new CaseBuilder()
                        .when(friend.status.eq(FriendStatus.Accept)
                                .and(friend.friend.eq(query
                                .selectFrom(user)
                                .where(user.eq(myUser))))).then(friend.user)
                        .when(friend.status.eq(FriendStatus.Accept)
                                .and(friend.user.eq(query
                                .selectFrom(user)
                                .where(user.eq(myUser))))).then(friend.friend)
                        .otherwise(myUser))
                .from(friend)
                .stream()
                .collect(Collectors.toSet());
    }

}
