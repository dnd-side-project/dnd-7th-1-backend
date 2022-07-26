package com.dnd.ground.domain.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @description 회원 엔티티
 * @author  박찬호, 박세헌
 * @since   2022-07-26
 * @updated 2022-07-26 / 회원 엔티티 생성 :박찬호
 */

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="user")
@Entity
public class User {

    @Id @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @Column(name = "username", nullable = false)
    private String userName;

    @Column(name = "nickname", nullable = false)
    private String nickName;

    @Column
    private String mail;

    @Column
    private String gender;

    @Column
    private double height;

    @Column
    private double weight;
}
