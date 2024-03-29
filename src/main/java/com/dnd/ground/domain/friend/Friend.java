package com.dnd.ground.domain.friend;

import com.dnd.ground.domain.user.User;
import lombok.*;

import javax.persistence.*;

/**
 * @description 친구 엔티티
 * @author  박찬호
 * @since   2022.07.28
 * @updated 1.상태 변경 메소드 추가
 *          -2022.10.10 박찬호
 */

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name="friend")
@Entity
public class Friend {

    @Id @GeneratedValue
    @Column(name = "friend_relation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id")
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

    //친구 상태 변경
    public void updateStatus(FriendStatus status) {
        this.status=status;
    }

}
