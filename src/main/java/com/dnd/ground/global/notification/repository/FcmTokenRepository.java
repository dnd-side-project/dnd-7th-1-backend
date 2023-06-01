package com.dnd.ground.global.notification.repository;

import com.dnd.ground.domain.user.UserPropertyFcmToken;
import com.dnd.ground.domain.user.repository.UserPropertyFcmTokenRepository;
import com.dnd.ground.global.notification.service.FcmService;
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
 * @updated 1.회원 탈퇴 API 구현 - 회원의 FCM 토큰 전체 삭제
 *          -2023-05-22 박찬호
 */

@Repository
@RequiredArgsConstructor
public class FcmTokenRepository {
    private final UserPropertyFcmTokenRepository userPropertyFcmTokenRepository;
    private final PhoneFcmTokenRepository phoneFcmTokenRepository;
    private final PadFcmTokenRepository padFcmTokenRepository;

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

    public String findToken(String nickname, DeviceType type) {
        if (type == DeviceType.PHONE) {
            Optional<PhoneFcmToken> phoneTokenOpt = phoneFcmTokenRepository.findById(nickname);
            if (phoneTokenOpt.isPresent()) {
                return phoneTokenOpt.get().getFcmToken();
            } else {
                return userPropertyFcmTokenRepository.findToken(nickname, DeviceType.PHONE)
                        .map(UserPropertyFcmToken::getFcmToken)
                        .get();
            }
        } else if (type == DeviceType.PAD) {
            Optional<PadFcmToken> padTokenOpt = padFcmTokenRepository.findById(nickname);
            if (padTokenOpt.isPresent()) {
                return padTokenOpt.get().getFcmToken();
            } else {
                return userPropertyFcmTokenRepository.findToken(nickname, DeviceType.PAD)
                        .map(UserPropertyFcmToken::getFcmToken)
                        .get();
            }
        } else return null;
    }

    public List<String> findAllTokens(String nickname) {
        List<String> tokens = new ArrayList<>();

        //핸드폰 토큰 조회
        Optional<PhoneFcmToken> phoneTokenOpt = phoneFcmTokenRepository.findById(nickname);
        if (phoneTokenOpt.isPresent()) tokens.add(phoneTokenOpt.get().getFcmToken());
        else {
            Optional<String> phoneTokenInDB = userPropertyFcmTokenRepository
                    .findToken(nickname, DeviceType.PHONE)
                    .map(UserPropertyFcmToken::getFcmToken);

            if (phoneTokenInDB.isPresent()) {
                String token = phoneTokenInDB.get();
                tokens.add(token);

                //재발급 요청
                FcmService.requestReissueFCMToken(nickname, token);
            }

        }

        //패드 토큰 조회
        Optional<PadFcmToken> padTokenOpt = padFcmTokenRepository.findById(nickname);
        if (padTokenOpt.isPresent()) tokens.add(padTokenOpt.get().getFcmToken());
        else {
            //재발급
            Optional<String> padTokenInDB = userPropertyFcmTokenRepository
                    .findToken(nickname, DeviceType.PAD)
                    .map(UserPropertyFcmToken::getFcmToken);

            if (padTokenInDB.isPresent()) {
                String token = padTokenInDB.get();
                tokens.add(token);

                FcmService.requestReissueFCMToken(nickname, token);
            }
        }

        return tokens;
    }

    public void deleteToken(String nickname, DeviceType type) {
        //레디스에 저장된 토큰 삭제
        if (type == DeviceType.PHONE) {
            phoneFcmTokenRepository
                    .findById(nickname)
                    .ifPresent(phoneFcmTokenRepository::delete);

        } else if (type == DeviceType.PAD) {
            padFcmTokenRepository
                    .findById(nickname)
                    .ifPresent(padFcmTokenRepository::delete);
        }

        userPropertyFcmTokenRepository.deleteByNicknameAndType(nickname, type);
    }

    @Transactional
    public void deleteToken(String nickname) {
        phoneFcmTokenRepository
                .findById(nickname)
                .ifPresent(phoneFcmTokenRepository::delete);

        padFcmTokenRepository
                .findById(nickname)
                .ifPresent(padFcmTokenRepository::delete);

        userPropertyFcmTokenRepository.deleteByNickname(nickname);
    }
}
