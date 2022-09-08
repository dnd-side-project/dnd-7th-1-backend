package com.dnd.ground.domain.user.service;

import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.JwtUserDto;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.CNotFoundException;
import com.dnd.ground.global.exception.CommonErrorCode;
import com.dnd.ground.global.util.AmazonS3Service;
import com.dnd.ground.global.util.JwtUtil;
import com.dnd.ground.global.util.JwtVerifyResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @description 회원의 인증/인가 및 회원 정보 관련 서비스 구현체
 * @author  박세헌, 박찬호
 * @since   2022-09-07
 * @updated 1.회원 인증/인가 및 로그인 관련 메소드 이동(UserService -> AuthService)
 *          2.닉네임 유효성 검사 기능 구현
 *          2022-09-07 박찬호
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService, UserDetailsService {

    private final UserRepository userRepository;
    private final AmazonS3Service amazonS3Service;

    /*회원 저장*/
    @Transactional
    public User save(JwtUserDto user){
        return userRepository.save(User
                .builder()
                .kakaoId(user.getId())
                .nickname(user.getNickname())
                .mail(user.getMail())
                .created(LocalDateTime.now())
                .intro("")
                .latitude(null)
                .longitude(null)
                .isShowMine(true)
                .isShowFriend(true)
                .isPublicRecord(true)
                .build());
    }

    /* 토큰으로 닉네임 찾은 후 반환하는 함수 */
    public ResponseEntity<Map<String, String>> getNicknameByToken(HttpServletRequest request){
        String accessToken = request.getHeader("Access-Token");
        JwtVerifyResult result = JwtUtil.verify(accessToken.substring("Bearer ".length()));

        Map<String, String> nick = new HashMap<>();
        nick.put("nickname", result.getNickname());

        return ResponseEntity.ok(nick);
    }

    /* AuthenticationManager가 User를 검증하는 함수 */
    @Override
    public UserDetails loadUserByUsername(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER)
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
        return nickname.length() >= 2 && nickname.length() <= 6 // 2~6글자
                && userRepository.findByNickname(nickname).isEmpty(); //중복X

    }

    //회원의 프로필 사진 변경
    public void updatePicture(User user, String pictureName, String picturePath) {
        //버킷에 있는 파일 삭제
        amazonS3Service.deleteFile(pictureName);

        //프로필 사진 변경
        user.updatePicture(pictureName, picturePath);
    }
}
