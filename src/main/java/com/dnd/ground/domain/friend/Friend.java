package com.dnd.ground.domain.friend;

import com.dnd.ground.domain.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @description 친구 엔티티
 * @author  박찬호
 * @since   2022-07-28
 * @updated 2022-07-28 / 엔티티 생성
 */

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="friend")
@Entity
public class Friend {

    @Id @GeneratedValue
    @Column(name = "friend_relation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user", referencedColumnName = "nickname")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend", referencedColumnName = "nickname")
    private User friend;

    @Enumerated(EnumType.STRING)
    @Column(name = "friend_status")
    private FriendStatus status;

    //Constructor
    public Friend(User user, User friend, FriendStatus status) {
        this.user = user;
        this.friend = friend;
        this.status = status;
    }

}
