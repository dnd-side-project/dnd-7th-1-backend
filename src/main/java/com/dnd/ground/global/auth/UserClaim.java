package com.dnd.ground.global.auth;

import com.dnd.ground.domain.user.LoginType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @description 토큰을 활용해 얻을 수 있는 UserClaim
 * @author 박찬호
 * @since 2023-01-25
 * @updated 1. UserClaim 생성
 *           - 2023.01.25 박찬호
 */

@Getter
@AllArgsConstructor
public class UserClaim {
    private String email;
    private String nickname;
    private LocalDateTime created;
    private LoginType loginType;

    public static long changeCreatedToLong(LocalDateTime created) {
        return created.toEpochSecond(ZoneOffset.of("+09:00"));
    }

    public static LocalDateTime changeCreatedToLocalDateTime(long epochSecond) {
        return LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.of("+09:00"));
    }
}
