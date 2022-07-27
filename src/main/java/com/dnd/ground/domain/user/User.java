package com.dnd.ground.domain.user;

import com.dnd.ground.domain.challenge.UserChallenge;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;

/**
 * @description 회원 엔티티
 * @author  박찬호, 박세헌
 * @since   2022-07-26
 * @updated 2022-07-27 / user, friends 컬럼 추가 및 유저챌린지 엔티티와 연관관계 매핑 :박찬호
 */

@Getter
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
    private double height;

    @Column
    private double weight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @OneToMany(mappedBy = "user")
    private List<User> friends;

    @OneToMany(mappedBy = "user")
    private List<UserChallenge> challenges = new ArrayList<>();
}
