package com.dnd.ground.global.securityFilter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.domain.user.service.UserService;
import com.dnd.ground.global.util.JwtUtil;
import com.dnd.ground.global.util.JwtVerifyResult;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.security.sasl.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @description 매 request마다 토큰을 검사해주는 필터
 * @author  박세헌, 박찬호
 * @since   2022-08-02
 * @updated 1. 생성
 *          - 2022.08.24 박세헌
 * @note 1. 매 request마다 토큰을 검사하여 securityContestHolder에 채워줌
 *       2. 해당 필터에서 자동 로그인을 구현 하면 될 것 같음
 *       (사용자가 어플을 키면 클라에서 토큰과 함께 임의의 uri로 요청을 보내면 되지 않을까..?)
 */

public class JWTCheckFilter extends BasicAuthenticationFilter {

    private final UserService userService;
    private final UserRepository userRepository;

    public JWTCheckFilter(AuthenticationManager authenticationManager,
                          UserService userService,
                          UserRepository userRepository)
    {
        super(authenticationManager);
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String accessToken = request.getHeader("access_token");
        String refreshToken = request.getHeader("access_token");

        // 왜 토큰이 안오죠? 넘어가
        if (accessToken == null || !accessToken.startsWith("Bearer ")){
            chain.doFilter(request, response);
            return;
        }

        // 액세스 토큰 verify
        String token = accessToken.substring("Bearer ".length());
        JwtVerifyResult result = JwtUtil.verify(token);

        // 클라에서 리프레시 토큰이 왔다는건 (액세스토큰은 만료된 것)
        if (refreshToken != null){
            // 리프레시 토큰도 만료됐다면
            if (!JwtUtil.verify(refreshToken.substring("Bearer ".length())).isSuccess()){
                throw new TokenExpiredException("토큰이 만료되었습니다!"); // 로그인 페이지로 가야해!
            }

            // 리프레시 토큰이 유효하다면
            else{
                User user = userRepository.findByNickname(result.getNickname()).orElseThrow();
                // 유저의 리프레시 토큰과 넘어온 리프레시 토큰이 같으면
                if (Objects.equals(user.getRefreshToken(), refreshToken.substring("Bearer ".length()))){
                    // 토큰 재발급, 리프레시 토큰은 저장
                    accessToken = JwtUtil.makeAccessToken(result.getNickname());
                    refreshToken = JwtUtil.makeRefreshToken(result.getNickname());
                    response.setHeader("aceess_token", "Bearer "+accessToken);
                    response.setHeader("refresh_token", "Bearer "+refreshToken);
                    user.updateRefreshToken(refreshToken);
                    userRepository.save(user);
                }
                else{
                    throw new AuthenticationException("잘못된 토큰 입니다.");
                }
            }
        }

        // 필터 통과
        UserDetails user = userService.loadUserByUsername(result.getNickname());
        UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(
                user.getUsername(), user.getPassword(), user.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(userToken);
        chain.doFilter(request, response);
    }
}