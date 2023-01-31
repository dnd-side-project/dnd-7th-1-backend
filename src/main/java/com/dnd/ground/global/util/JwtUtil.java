package com.dnd.ground.global.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dnd.ground.global.exception.AuthException;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;

import static com.dnd.ground.global.exception.ExceptionCodeSet.*;

/**

 * @description 토큰 생성 및 유효성 검사를 위한 Util
 * @author 박찬호
 * @since 2023-01-21
 * @updated 1. 토큰 Payload 변경
 *          - 2023.01.27 박찬호
 */

@Component
@NoArgsConstructor
public class JwtUtil {
    private static String SECRET_KEY;
    private static String ISSUER;

    private static final String TYPE = "type";
    private static final long ACCESS_TIME = 60 * 3600;  // 액세스 토큰 6시간
    private static final long REFRESH_TIME = 60 * 60 * 24 * 55;  // 리프레시 토큰 약 2달

    @Value("${jwt.secret_key}")
    public void setSecretKey(String secretKey) {
        JwtUtil.SECRET_KEY = secretKey;
    }

    @Value("${jwt.issuer}")
    public void setIssuer(String issuer) {
        JwtUtil.ISSUER = issuer;
    }

    /*Access token provider*/
    public static String createAccessToken(String email, LocalDateTime created) {
        final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET_KEY);

        return JWT.create()
                .withIssuer(ISSUER)
                .withExpiresAt(Date.from(Instant.now().plusSeconds(ACCESS_TIME)))
                .withIssuedAt(Date.from(Instant.now()))
                .withNotBefore(Date.valueOf(created.toLocalDate()))
                .withSubject(email)
                .withClaim(TYPE, "access")
                .sign(ALGORITHM);
    }

    /*Refresh token provider*/
    public static String createRefreshToken(String email, LocalDateTime created) {
        final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET_KEY);

        return JWT.create()
                .withIssuer(ISSUER)
                .withExpiresAt(Date.from(Instant.now().plusSeconds(REFRESH_TIME)))
                .withNotBefore(Date.valueOf(created.toLocalDate()))
                .withSubject(email)
                .withClaim(TYPE, "refresh")
                .sign(ALGORITHM);
    }

    public static boolean accessVerify(String token) {
        final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET_KEY);

        DecodedJWT verify;
        try {
            verify = JWT.require(ALGORITHM).build().verify(token);
        } catch (IllegalArgumentException | AlgorithmMismatchException e) {
            throw new AuthException(ALGORITHM_INVALID);
        } catch (SignatureVerificationException e) {
            throw new AuthException(SIGNATURE_INVALID);
        } catch (TokenExpiredException e) {
            throw new AuthException(ACCESS_TOKEN_EXPIRED);
        } catch (JWTVerificationException e) {
            throw new AuthException(REFRESH_TOKEN_INVALID);
        }

        if (!verify.getIssuer().equals(ISSUER)) throw new AuthException(ISS_INVALID);
        else if (verify.getExpiresAt().before(Date.from(Instant.now()))) throw new AuthException(EXP_INVALID);
        else if (verify.getNotBefore().after(Date.from(Instant.now()))) throw new AuthException(NBF_INVALID);

        return true;
    }

    public static boolean refreshVerify(String token) {
        final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET_KEY);

        DecodedJWT verify;
        try {
            verify = JWT.require(ALGORITHM).build().verify(token);
        } catch (IllegalArgumentException | AlgorithmMismatchException e) {
            throw new AuthException(ALGORITHM_INVALID);
        } catch (SignatureVerificationException e) {
            throw new AuthException(SIGNATURE_INVALID);
        } catch (TokenExpiredException e) {
            throw new AuthException(REFRESH_TOKEN_EXPIRED);
        } catch (JWTVerificationException e) {
            throw new AuthException(REFRESH_TOKEN_INVALID);
        }

        if (!verify.getIssuer().equals(ISSUER)) throw new AuthException(ISS_INVALID);

        return true;
    }
}