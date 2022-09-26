package com.dnd.ground.global.securityFilter;

import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.KakaoDto;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.domain.user.service.KakaoService;
import com.dnd.ground.global.exception.CommonErrorCode;
import com.dnd.ground.global.util.JwtUtil;
import org.json.JSONObject;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * @description 로그인 필터
 * @author  박찬호
 * @since   2022-09-25
 * @updated 1. 로그인 필터 생성
 *          - 2022.09.25 박찬호
 *
 * @note 1."/login"으로 헤더에 카카오 엑세스 토큰을 담아서 요청이 오면,
 *         헤더에 자체 엑세스, 리프레시 토큰을 담고 바디에 닉네임을 담아서 Response.
 */
public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final KakaoService kakaoService;
    private final UserRepository userRepository;
    private static User user;

    //Constructor
    public JWTLoginFilter(AuthenticationManager authenticationManager,
                          KakaoService kakaoService,
                          UserRepository userRepository) {
        super(authenticationManager);
        this.kakaoService = kakaoService;
        this.userRepository = userRepository;
        setFilterProcessesUrl("/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String kakaoAccessToken = request.getHeader("Kakao-Access-Token");
        KakaoDto.TokenInfo tokenInfo = null;

        try {
            tokenInfo = kakaoService.getTokenInfo(kakaoAccessToken);
        } catch (WebClientResponseException e) { //카카오에서 -401 받는 예외 처리(잘못된 토큰)
            request.setAttribute("exception", CommonErrorCode.WRONG_TOKEN.getMessage());
        }

        Optional<User> findUser = userRepository.findByKakaoId(tokenInfo.getId());

        if (findUser.isPresent()) {
            user = findUser.get();
            String nickname = user.getNickname();

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    nickname,
                    tokenInfo.getId()+nickname,
                    null
            );

            return getAuthenticationManager().authenticate(token);
        }
        else {
            request.setAttribute("exception", CommonErrorCode.NOT_SIGNUP_USER);
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException
    {
        // 회원 조회
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) authResult.getPrincipal();
        String accessToken = JwtUtil.makeAccessToken(principal.getUsername());
        String refreshToken = JwtUtil.makeRefreshToken(principal.getUsername());

        // 토큰 발급 및 DB 최신화
        response.setHeader("Authorization", "Bearer "+ accessToken);
        response.setHeader("Refresh-Token", "Bearer "+ refreshToken);
        response.setContentType("application/json; charset=utf-8");

        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        // 닉네임과 함께 response
        JSONObject json = new JSONObject();
        json.put("nickname", principal.getUsername());
        response.getWriter().write(String.valueOf(json));
    }
}