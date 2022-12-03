package com.dnd.ground.domain.user.service;

import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.JwtUserDto;
import com.dnd.ground.domain.user.dto.KakaoDto;
import com.dnd.ground.domain.user.dto.UserRequestDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.exception.UserException;
import com.dnd.ground.global.util.AmazonS3Service;
import com.dnd.ground.global.util.JwtUtil;
import com.dnd.ground.global.util.JwtVerifyResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @description 회원의 인증/인가 및 회원 정보 관련 서비스 구현체
 * @author  박세헌, 박찬호
 * @since   2022-09-07
 * @updated 1.예외 처리 리팩토링
 *          - 2022-12-03 박찬호
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService, UserDetailsService {

    private final UserRepository userRepository;
    private final KakaoService kakaoService;

    /*회원 저장*/
    @Transactional
    public User save(JwtUserDto user){
        return userRepository.save(User.builder()
                .kakaoId(user.getId())
                .kakaoRefreshToken(user.getKakaoRefreshToken())
                .nickname(user.getNickname())
                .mail(user.getMail())
                .created(LocalDateTime.now())
                .intro("")
                .latitude(null)
                .longitude(null)
                .isShowMine(true)
                .isShowFriend(true)
                .isPublicRecord(user.getIsPublicRecord())
                .pictureName(user.getPictureName())
                .picturePath(user.getPicturePath())
                .build());
    }

    /*회원 가입*/
    public ResponseEntity<UserResponseDto.SignUp> signUp(String kakaoAccessToken, UserRequestDto.SignUp request) throws ParseException, UnknownHostException {

        //카카오 회원 정보 조회(카카오 ID, 이메일, 프로필 사진)
        KakaoDto.UserInfo kakaoUserInfo = kakaoService.getUserInfo(kakaoAccessToken);

        WebClient webClient = WebClient.create();

        JwtUserDto jwtUserDto = JwtUserDto.builder()
                .id(kakaoUserInfo.getId())
                .kakaoRefreshToken(request.getKakaoRefreshToken())
                .nickname(request.getNickname())
                .isPublicRecord(request.getIsPublicRecord())
                .mail(kakaoUserInfo.getEmail())
                .pictureName(kakaoUserInfo.getPictureName())
                .picturePath(kakaoUserInfo.getPicturePath())
                .build();


        //필터 호출
        ResponseEntity<UserResponseDto.SignUp> response = webClient.post()
                .uri("http://"+InetAddress.getLocalHost().getHostAddress()+":8080/sign")//서버 배포시 서버에 할당된 IP로 변경 예정
                .body(Mono.just(jwtUserDto), JwtUserDto.class)
                .retrieve()
                .toEntity(UserResponseDto.SignUp.class)
                .block();

        return response;
    }

    /* 토큰으로 닉네임 찾은 후 반환하는 함수 */
    public ResponseEntity<Map<String, String>> getNicknameByToken(HttpServletRequest request){
        String accessToken = request.getHeader("Authorization");
        String refreshToken = request.getHeader("Refresh-Token");

        JwtVerifyResult result = null;
        if (accessToken != null) {
            result = JwtUtil.verify(accessToken.substring("Bearer ".length()));
        }
        else{
            result = JwtUtil.verify(refreshToken.substring("Bearer ".length()));
        }

        Map<String, String> nick = new HashMap<>();
        nick.put("nickname", result.getNickname());

        return ResponseEntity.ok(nick);
    }

    /* AuthenticationManager가 User를 검증하는 함수 */
    @Override
    public UserDetails loadUserByUsername(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND)
        );

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return org.springframework.security.core.userdetails.User.builder()
                .username(nickname)
                .password(passwordEncoder.encode(user.getKakaoId()+user.getNickname()))
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

    /*기존 유저인지 판별*/
    public Boolean isOriginalUser(HttpServletRequest request) {
        String accessToken = request.getHeader("Kakao-Access-Token");

        KakaoDto.TokenInfo tokenInfo = kakaoService.getTokenInfo(accessToken);

        return userRepository.findByKakaoId(tokenInfo.getId()).isPresent();
    }

    /* 리프레시 토큰이 오면 JWTCheckFilter에서 검증 후 성공적으로 filter를 통과 했다면 해당 로직에서 토큰 재발급 */
    public ResponseEntity<Boolean> issuanceToken(String refreshToken){

        String token = refreshToken.substring("Bearer ".length());
        JwtVerifyResult result = JwtUtil.verify(token);
        // 토큰 재발급, 리프레시 토큰은 저장
        String accessToken = JwtUtil.makeAccessToken(result.getNickname());
        refreshToken = JwtUtil.makeRefreshToken(result.getNickname());

        User user = userRepository.findByNickname(result.getNickname()).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Refresh-Token", "Bearer " + refreshToken);

        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(true);
    }
    /* 새로운 닉네임으로 토큰 재발급 */
    public ResponseEntity<UserResponseDto.UInfo> issuanceTokenByNickname(String nickname){
        String accessToken = JwtUtil.makeAccessToken(nickname);
        String refreshToken = JwtUtil.makeRefreshToken(nickname);

        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Refresh-Token", "Bearer " + refreshToken);

        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new UserResponseDto.UInfo(user.getNickname(), user.getPicturePath()));
    }

}
