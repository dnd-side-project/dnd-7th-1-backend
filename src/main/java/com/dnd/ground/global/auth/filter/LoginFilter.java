package com.dnd.ground.global.auth.filter;

import com.dnd.ground.domain.user.LoginType;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.KakaoDto;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.auth.UserClaim;
import com.dnd.ground.global.auth.dto.SocialResponseDto;
import com.dnd.ground.global.auth.dto.UserLoginDto;
import com.dnd.ground.global.auth.service.AppleService;
import com.dnd.ground.global.auth.service.KakaoService;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.exception.FilterException;
import com.dnd.ground.global.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @description 소셜 로그인 후, 자체 토큰 발급을 위한 로그인 필터
 *              인증서버로부터 받는 토큰의 값과 전달 받은 username 값 비교 후 토큰 발급.
 * @author  박찬호
 * @since   2023-01-29
 * @updated 1. 새로운 필터 생성
 *          - 2023.01.29 박찬호
 */
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final UserRepository userRepository;
    private final KakaoService kakaoService;
    private final AppleService appleService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private User user;
    private static final String BEARER = "Bearer ";

    //Constructor
    public LoginFilter(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       KakaoService kakaoService,
                       AppleService appleService,
                       AuthenticationEntryPoint authenticationEntryPoint) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.kakaoService = kakaoService;
        this.appleService = appleService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        setFilterProcessesUrl("/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String oauthToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        UserLoginDto loginDto;
        try {
            loginDto = objectMapper.readValue(request.getInputStream(), UserLoginDto.class);
        } catch (IOException e) {
            throw new FilterException(ExceptionCodeSet.INTERNAL_SERVER_ERROR);
        }

        String emailFromOauth = null;

        if (loginDto == null || oauthToken == null) throw new FilterException(ExceptionCodeSet.BAD_REQUEST);
        else {
            if (loginDto.getLoginType().equals(LoginType.KAKAO)) {
                oauthToken = oauthToken.substring(BEARER.length());
                KakaoDto.UserInfo userInfo = kakaoService.getUserInfo(oauthToken);
                emailFromOauth = userInfo.getEmail();
            } else if (loginDto.getLoginType().equals(LoginType.APPLE)) {
                SocialResponseDto userInfo = appleService.appleLogin(oauthToken);
                emailFromOauth = userInfo.getEmail();
            }
        }

        String email = loginDto.getEmail();
        if (!emailFromOauth.equals(email)) throw new FilterException(ExceptionCodeSet.WRONG_TOKEN);

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            user = userOpt.get();
            long createdMilli = UserClaim.changeCreatedToLong(user.getCreated());

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    email, user.getNickname() + "-" + createdMilli
            );

            return getAuthenticationManager().authenticate(authenticationToken);
        } else throw new FilterException(ExceptionCodeSet.USER_NOT_FOUND);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException {
        // 회원 조회
        String email = authResult.getName();
        LocalDateTime created = user.getCreated();
        String nickname = user.getNickname();

        String accessToken = JwtUtil.createAccessToken(email, created);
        String refreshToken = JwtUtil.createRefreshToken(email, created);

        //Set response character
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");

        //Set response data
        response.setHeader(HttpHeaders.AUTHORIZATION, BEARER + accessToken);
        response.setHeader("Refresh-Token", BEARER + refreshToken);

        JSONObject json = new JSONObject();
        json.put("nickname", nickname);
        response.getWriter().write(String.valueOf(json));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        authenticationEntryPoint.commence(request, response, failed);
    }
}
