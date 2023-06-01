package com.dnd.ground.global.auth.service;

import com.amazonaws.util.StringUtils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dnd.ground.domain.challenge.service.ChallengeService;
import com.dnd.ground.domain.exerciseRecord.service.ExerciseRecordService;
import com.dnd.ground.domain.friend.service.FriendService;
import com.dnd.ground.domain.user.LoginType;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.UserProperty;
import com.dnd.ground.domain.user.repository.UserPropertyRepository;
import com.dnd.ground.global.notification.cache.PadFcmToken;
import com.dnd.ground.global.notification.repository.FcmTokenRepository;
import com.dnd.ground.global.auth.UserClaim;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.auth.dto.FcmTokenUpdateDto;
import com.dnd.ground.global.auth.dto.UserSignDto;
import com.dnd.ground.global.exception.*;
import com.dnd.ground.global.notification.cache.PhoneFcmToken;
import com.dnd.ground.global.util.DeviceType;
import com.dnd.ground.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @description 회원의 인증/인가 및 회원 정보 관련 서비스 구현체
 * @author  박찬호
 * @since   2022-09-07
 * @updated 1. 회원 탈퇴 API 수정
 *          - 2023.05.23 박찬호
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService, UserDetailsService {

    private final UserRepository userRepository;
    private final UserPropertyRepository userPropertyRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final FriendService friendService;
    private final ChallengeService challengeService;
    private final ExerciseRecordService exerciseRecordService;
    private final KakaoService kakaoService;

    @Value("${jwt.secret_key}")
    private String SECRET_KEY;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 회원저장 V1
     * @deprecated
     */
//    @Transactional
//    public User save(JwtUserDto user){
//        return userRepository.save(User.builder()
//                .nickname(user.getNickname())
//                .email(user.getMail())
//                .created(LocalDateTime.now())
//                .intro("")
//                .latitude(null)
//                .longitude(null)
//                .isShowMine(true)
//                .isShowFriend(true)
//                .isPublicRecord(user.getIsPublicRecord())
//                .pictureName(user.getPictureName())
//                .picturePath(user.getPicturePath())
//                .build());
//    }

    /**
     * 회원가입 V1
     * @deprecated
     * */
//    public ResponseEntity<UserResponseDto.SignUp> signUp(String kakaoAccessToken, UserRequestDto.SignUp request) throws ParseException, UnknownHostException {
//
//        //카카오 회원 정보 조회(카카오 ID, 이메일, 프로필 사진)
//        KakaoDto.UserInfo kakaoUserInfo = kakaoService.getUserInfo(kakaoAccessToken);
//
//        WebClient webClient = WebClient.create();
//
//        JwtUserDto jwtUserDto = JwtUserDto.builder()
//                .id(kakaoUserInfo.getKakaoId())
//                .kakaoRefreshToken(request.getKakaoRefreshToken())
//                .nickname(request.getNickname())
//                .isPublicRecord(request.getIsPublicRecord())
//                .mail(kakaoUserInfo.getEmail())
//                .pictureName(kakaoUserInfo.getPictureName())
//                .picturePath(kakaoUserInfo.getPicturePath())
//                .build();
//
//        //필터 호출
//        ResponseEntity<UserResponseDto.SignUp> response = webClient.post()
//                .uri("http://"+IP+":8080/sign")//서버 배포시 서버에 할당된 IP로 변경 예정
//                .body(Mono.just(jwtUserDto), JwtUserDto.class)
//                .retrieve()
//                .toEntity(UserResponseDto.SignUp.class)
//                .block();
//
//        return response;
//    }

    @Transactional
    public UserSignDto.Response signUp(UserSignDto signDto, HttpServletResponse response) {
        //닉네임 중복 확인
        String nickname = signDto.getNickname();
        if (userRepository.findByNickname(nickname).isPresent())
            throw new AuthException(ExceptionCodeSet.DUPLICATE_NICKNAME);

        //회원 생성
        Optional<User> userOpt = userRepository.findByEmail(signDto.getEmail());
        if (userOpt.isEmpty()) {
            User user = User.builder()
                    .created(LocalDateTime.now())
                    .nickname(nickname)
                    .email(signDto.getEmail())
                    .intro(null)
                    .latitude(null)
                    .longitude(null)
                    .pictureName(signDto.getPictureName())
                    .picturePath(signDto.getPicturePath())
                    .loginType(signDto.getLoginType())
                    .build();

            Boolean isNotification = signDto.getIsNotification();
            UserProperty property = UserProperty.builder()
                    .socialId(signDto.getSocialId())
                    .isExceptRecommend(signDto.getIsExceptRecommend())
                    .isShowMine(true)
                    .isShowFriend(true)
                    .isPublicRecord(signDto.getIsPublicRecord())
                    .notiWeekStart(isNotification)
                    .notiWeekEnd(isNotification)
                    .notiFriendRequest(isNotification)
                    .notiFriendAccept(isNotification)
                    .notiChallengeRequest(isNotification)
                    .notiChallengeAccept(isNotification)
                    .notiChallengeStart(isNotification)
                    .notiChallengeCancel(isNotification)
                    .notiChallengeResult(isNotification)
                    .build();

            user.setUserProperty(property);
            userRepository.save(user);

            if (signDto.getDeviceType() == DeviceType.PHONE) fcmTokenRepository.save(new PhoneFcmToken(nickname, signDto.getFcmToken(), 60L));
            else if (signDto.getDeviceType() == DeviceType.PAD) fcmTokenRepository.save(new PadFcmToken(nickname, signDto.getFcmToken(), 60L));

            //친구 신청
            for (String friendNickname : signDto.getFriends()) {
                Boolean result = friendService.requestFriend(nickname, friendNickname);
                if (!result) throw new FriendException(ExceptionCodeSet.FRIEND_NOT_FOUND);
            }

            response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + JwtUtil.createAccessToken(user.getEmail(), user.getCreated()));
            response.setHeader("Refresh-Token", "Bearer " + JwtUtil.createRefreshToken(user.getEmail(), user.getCreated()));
            return new UserSignDto.Response(nickname);

        } else throw new AuthException(ExceptionCodeSet.SIGN_DUPLICATED);
    }

    /* AuthenticationManager가 User를 검증하는 함수 */
    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new FilterException(ExceptionCodeSet.USER_NOT_FOUND)
        );

        long createdMilli = UserClaim.changeCreatedToLong(user.getCreated());

        return org.springframework.security.core.userdetails.User.builder()
                .username(email)
                .password(passwordEncoder.encode(user.getNickname()+"-"+createdMilli))
                .authorities("BASIC")
                .build();
    }

    /**--회원 정보 관련 로직--**/

    /*닉네임 Validation*/
    public Boolean validateNickname(String nickname) {
        Pattern rex = Pattern.compile("[^\uAC00-\uD7A3xfe0-9a-zA-Z]");
        return nickname.length() >= 2 && nickname.length() <= 6 // 2~6글자
                && userRepository.findByNickname(nickname).isEmpty() //중복X
                && !rex.matcher(nickname).find(); // 특수문자
    }

    /*토큰을 통해 UserClaim 반환*/
    public UserClaim getUserClaim(String token) {
        final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET_KEY);

        DecodedJWT decodedToken = JWT.require(ALGORITHM).build().verify(token);
        String email = decodedToken.getSubject();

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return new UserClaim(email, user.getNickname(), user.getCreated(), user.getLoginType());
        } else throw new AuthException(ExceptionCodeSet.WRONG_TOKEN);
    }

    /*DB의 FCM 토큰 최신화*/
    @Override
    @Transactional
    public ExceptionCodeSet updateFcmToken(FcmTokenUpdateDto request) {
        if (request.getDeviceType() == DeviceType.PHONE) {
            fcmTokenRepository.save(new PhoneFcmToken(request.getNickname(), request.getFcmToken(), 60L));
        } else if (request.getDeviceType() == DeviceType.PAD) {
            fcmTokenRepository.save(new PadFcmToken(request.getNickname(), request.getFcmToken(), 60L));
        } else throw new CommonException(ExceptionCodeSet.DEVICE_TYPE_INVALID);

        return ExceptionCodeSet.OK;
    }


    /*로그아웃*/
    @Override
    @Transactional
    public ExceptionCodeSet logout(String nickname, DeviceType deviceType) {
        fcmTokenRepository.deleteToken(nickname, deviceType);

        return ExceptionCodeSet.OK;
    }

    /*회원 탈퇴*/
    @Override
    @Transactional
    public ExceptionCodeSet deleteUser(String nickname, String kakaoToken, LoginType loginType) {
        User user = userRepository.findByNicknameWithProperty(nickname)
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        deleteUser(user);

        if (loginType == LoginType.KAKAO) {
            if (StringUtils.isNullOrEmpty(kakaoToken)) throw new AuthException(ExceptionCodeSet.SOCIAL_ID_INVALID);

            try {
                kakaoService.unlink(kakaoToken, Long.parseLong(user.getProperty().getSocialId()));
            } catch (NumberFormatException e) {
                throw new AuthException(ExceptionCodeSet.SOCIAL_ID_INVALID);
            }
        }

        return ExceptionCodeSet.OK;
    }

    @Override
    @Transactional
    public ExceptionCodeSet deleteUser(User user) {
        //운동 기록, 영역 삭제
        exerciseRecordService.deleteRecordAll(user);

        //친구 삭제
        friendService.deleteFriendAll(user);

        //챌린지 삭제
        challengeService.convertDeleteUser(user);

        //회원정보 삭제
        fcmTokenRepository.deleteToken(user.getNickname()); //FCM 토큰 삭제
        userRepository.delete(user);//회원 삭제
        userPropertyRepository.delete(user.getProperty()); //회원 정보 삭제

        return ExceptionCodeSet.OK;
    }
}