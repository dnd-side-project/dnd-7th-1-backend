package com.dnd.ground.global.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.global.auth.dto.ApplePublicKeyResponseDto;
import com.dnd.ground.global.auth.dto.SocialResponseDto;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.AuthException;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Date;
import java.util.Optional;

import static com.dnd.ground.global.exception.ExceptionCodeSet.*;

/**
 * @description 애플과 관련한 서비스 클래스
 * @author  박찬호
 * @since   2023.01.20
 * @updated 1. idToken 검증 및 회원 정보 반환 API 생성
 *           - 2022-01-20 박찬호
 */

@RequiredArgsConstructor
@Service
public class AppleService {
    @Value("${apple.ISS}")
    private String ISS;

    @Value("${apple.AUD}")
    private String AUD;

    @Value("${picture.path}")
    private String DEFAULT_PATH;

    @Value("${picture.name}")
    private String DEFAULT_NAME;

    private final UserRepository userRepository;

    /*프론트로부터 받은 IdToken 검증 후 SocialResponseDto 반환*/
    public SocialResponseDto appleLogin(String idToken) {
        String email;
        boolean isSigned;
        String picturePath;
        String pictureName;

        DecodedJWT decodedIdToken = JWT.decode(idToken);
        ExceptionCodeSet verifyResult = verifyIdToken(decodedIdToken);

        if (verifyResult.equals(OK)) {
            String emailVerified = decodedIdToken.getClaim("email_verified").asString();
            if (emailVerified.equals("true")) {
                email = decodedIdToken.getClaim("email").asString();
                Optional<User> userOpt = userRepository.findByEmail(email);

                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    picturePath = user.getPicturePath();
                    pictureName = user.getPictureName();
                    isSigned = true;
                } else {
                    pictureName = DEFAULT_NAME;
                    picturePath = DEFAULT_PATH;
                    isSigned = false;
                }
                return new SocialResponseDto(email, picturePath, pictureName, isSigned);
            } else {
                throw new AuthException(EMAIL_NOT_VERIFY);
            }

        } else throw new AuthException(verifyResult);
    }

    /*idToken 검증*/
    private ExceptionCodeSet verifyIdToken(DecodedJWT decodedIdToken) {
        Date expiresAt = decodedIdToken.getExpiresAt();
        //EXP verify
        if (new Date(System.currentTimeMillis()).after(expiresAt)) return ID_TOKEN_EXPIRED;

        //ISS verify
        if (!ISS.equals(decodedIdToken.getIssuer())) return ID_TOKEN_INVALID;

        //AUD verify
        if (!AUD.equals(decodedIdToken.getAudience().get(0))) return ID_TOKEN_INVALID;

        //RSA verify
        WebClient webClient = WebClient.create();

        ApplePublicKeyResponseDto publicKey = webClient.get()
                .uri("https://appleid.apple.com/auth/keys")
                .retrieve()
                .bodyToMono(ApplePublicKeyResponseDto.class)
                .block();

        Optional<ApplePublicKeyResponseDto.Key> matchedKeyOpt = publicKey.getMatchedKey(decodedIdToken.getKeyId(), decodedIdToken.getAlgorithm());
        if (matchedKeyOpt.isEmpty()) return ID_TOKEN_INVALID;

        return OK;
    }
}