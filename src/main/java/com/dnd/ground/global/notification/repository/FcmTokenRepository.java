package com.dnd.ground.global.notification.repository;

import com.dnd.ground.domain.user.UserPropertyFcmToken;
import com.dnd.ground.domain.user.repository.UserPropertyFcmTokenRepository;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.exception.UserException;
import com.dnd.ground.global.notification.NotificationService;
import com.dnd.ground.global.notification.cache.PadFcmToken;
import com.dnd.ground.global.notification.cache.PhoneFcmToken;
import com.dnd.ground.global.util.DeviceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @description 디바이스 종류 별로 캐싱되어 있는 FCM 토큰을 조회하기 위한 Repository
 *              해당 계층에서 FCM 토큰과 관련한 공통된 정보를 처리한다.
 * @author  박찬호
 * @since   2023-05-11
 * @updated 1. 객체 저장 및 조회 메소드 구현
 *          -2023-05-11 박찬호
 */

@Repository
@RequiredArgsConstructor
public class FcmTokenRepository {
    private final UserPropertyFcmTokenRepository userPropertyFcmTokenRepository;
    private final PhoneFcmTokenRepository phoneFcmTokenRepository;
    private final PadFcmTokenRepository padFcmTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public void save(PhoneFcmToken phoneFcmToken) {
        //DB 저장
        Optional<UserPropertyFcmToken> tokenInDBOpt = userPropertyFcmTokenRepository.findToken(phoneFcmToken.getNickname(), DeviceType.PHONE);

        if (tokenInDBOpt.isPresent()) {
            UserPropertyFcmToken userPropertyFcmToken = tokenInDBOpt.get();
            userPropertyFcmToken.setFcmToken(userPropertyFcmToken.getFcmToken());
        } else {
            userPropertyFcmTokenRepository.save(new UserPropertyFcmToken(phoneFcmToken.getNickname(), DeviceType.PHONE, phoneFcmToken.getFcmToken()));
        }

        //레디스 저장
        phoneFcmTokenRepository.save(phoneFcmToken);
    }

    @Transactional
    public void save(PadFcmToken padFcmToken) {
        //DB 저장
        Optional<UserPropertyFcmToken> tokenInDBOpt = userPropertyFcmTokenRepository.findToken(padFcmToken.getNickname(), DeviceType.PAD);

        if (tokenInDBOpt.isPresent()) {
            UserPropertyFcmToken userPropertyFcmToken = tokenInDBOpt.get();
            userPropertyFcmToken.setFcmToken(userPropertyFcmToken.getFcmToken());
        } else {
            userPropertyFcmTokenRepository.save(new UserPropertyFcmToken(padFcmToken.getNickname(), DeviceType.PAD, padFcmToken.getFcmToken()));
        }

        //레디스 저장
        padFcmTokenRepository.save(padFcmToken);
    }

    public List<String> findAllTokens(String nickname) {
        List<String> tokens = new ArrayList<>();

        //핸드폰 토큰 조회
        Optional<PhoneFcmToken> phoneTokenOpt = phoneFcmTokenRepository.findById(nickname);
        if (phoneTokenOpt.isPresent()) tokens.add(phoneTokenOpt.get().getFcmToken());
        else {
            //재발급
            NotificationService.requestReissueFCMToken(
                    userRepository.findByNickname(nickname).orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND)),
                    DeviceType.PHONE
            );

            userPropertyFcmTokenRepository.findToken(nickname, DeviceType.PHONE)
                    .map(UserPropertyFcmToken::getFcmToken)
                    .ifPresent(tokens::add);
        }

        //패드 토큰 조회
        Optional<PadFcmToken> padTokenOpt = padFcmTokenRepository.findById(nickname);
        if (padTokenOpt.isPresent()) tokens.add(padTokenOpt.get().getFcmToken());
        else {
            //재발급
            NotificationService.requestReissueFCMToken(
                    userRepository.findByNickname(nickname).orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND)),
                    DeviceType.PAD
            );

            userPropertyFcmTokenRepository.findToken(nickname, DeviceType.PAD)
                    .map(UserPropertyFcmToken::getFcmToken)
                    .ifPresent(tokens::add);
        }

        return tokens;
    }
}
