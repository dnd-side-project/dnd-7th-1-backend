package com.dnd.ground.global.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
;
import java.time.Instant;

/**
 * @description JWT 관련 util(토큰 생성, 유효성 검사)
 * @author  박세헌
 * @since   2022-08-24
 * @updated 1. JWT Util 클래스 생성
 *          - 2022.08.24 박세헌
 */

public class JwtUtil {

    private static final Algorithm ALGORITHM = Algorithm.HMAC256("token-secret-key");
    private static final long ACCESS_TIME = 60*30;  // 액세스 토큰 30분
    private static final long REFRESH_TIME = 60*60*24*14;  // 리프레시 토큰 2주

    // 액세스 토큰 생성
    public static String makeAccessToken(String name){
        return JWT.create()
                .withClaim("exp", Instant.now().getEpochSecond()+ACCESS_TIME)
                .withSubject(name)
                .sign(ALGORITHM);
    }

    // 리프레시 토큰 생성
    public static String makeRefreshToken(String name){
        return JWT.create()
                .withClaim("exp", Instant.now().getEpochSecond()+REFRESH_TIME)
                .withSubject(name)
                .sign(ALGORITHM);
    }

    // 유효성 검사(토큰 subject, 유효성 여부)
    public static JwtVerifyResult verify(String token){
        try {
            DecodedJWT verify = JWT.require(ALGORITHM).build().verify(token);
            return JwtVerifyResult.builder().success(true)
                    .nickname(verify.getSubject()).build();
        } catch (Exception ex){
            DecodedJWT decode = JWT.decode(token);
            return JwtVerifyResult.builder().success(false)
                    .nickname(decode.getSubject()).build();
        }
    }

}
