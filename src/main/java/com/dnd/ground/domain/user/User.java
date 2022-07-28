package com.dnd.ground.domain.user;

import com.dnd.ground.domain.challenge.UserChallenge;
import com.dnd.ground.domain.friend.Friend;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;

/**
 * @description 회원 엔티티
 * @author  박찬호, 박세헌
 * @since   2022-07-26
 * @updated 1. Friend와 연관관계 매핑
 *          2. Builder 패턴 적용
 *          - 박찬호
 */

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="user")
@Entity
public class User {

    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username", nullable = false)
    private String userName;

    @Column(name = "nickname", nullable = false)
    private String nickName;

    @Email
    @Column
    private String mail;

    @Column
    private String gender;

    @Column
    private Double height;

    @Column
    private Double weight;

    @OneToMany(mappedBy = "friend")
    private List<Friend> friends = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UserChallenge> challenges = new ArrayList<>();
}
