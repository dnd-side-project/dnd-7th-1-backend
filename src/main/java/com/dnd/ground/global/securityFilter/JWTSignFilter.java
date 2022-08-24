package com.dnd.ground.global.securityFilter;

import com.dnd.ground.domain.user.dto.JwtUserDto;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.domain.user.service.UserService;
import com.dnd.ground.global.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description 회원가입 혹은 재로그인시 인증 필터
 * @author  박세헌
 * @since   2022-08-24
 * @updated 1. 필터 생성
 *          2. password: kakaoId + 닉네임
 *          - 2022.08.24 박세헌
 * @note 1. 찬호가 카카오 유저 대한 정보를 JwtUserDto에 맞게 "/sign"으로 post요청(회원가입 or 재로그인)
 *       2. 기존 유저가 자동 로그인 되는 기능은 어디서 처리? 해당 필터에서 처리 가능 할 것 같지만 매우매우 복잡
 */

public class JWTSignFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserService userService;
    private final UserRepository userRepository;

    public JWTSignFilter(AuthenticationManager authenticationManager,
                         UserService userService,
                         UserRepository userRepository)
    {
        super(authenticationManager);
        this.userService = userService;
        this.userRepository = userRepository;
        setFilterProcessesUrl("/sign");
    }

    // 회원 가입
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException
    {
        // 정보를 JwtUSerDto에 저장
        JwtUserDto userDto = null;
        try {
            userDto = objectMapper.readValue(request.getInputStream(), JwtUserDto.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 신규 회원이면 저장
        if (!userRepository.existsByKakaoId(userDto.getId())){
            userService.save(userDto);
        }

        // id: 닉네임, password: kakaoId + 닉네임
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                userDto.getNickname(),
                userDto.getId()+userDto.getNickname(),
                null
        );

        // AuthenticationManager에게 인증 요청
        return getAuthenticationManager().authenticate(token);
    }

    // 성공적으로 인증이 되었다면 넘어옴 해당 함수로 넘어옴
    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException
    {
        // 유저 찾아서
        User principal = (User) authResult.getPrincipal();
        String accessToken = JwtUtil.makeAccessToken(principal.getUsername());
        String refreshToken = JwtUtil.makeAccessToken(principal.getUsername());

        // Jwt토큰 발급, refresh 토큰은 저장
        response.setHeader("aceess_token", "Bearer "+accessToken);
        response.setHeader("refresh_token", "Bearer "+refreshToken);
        com.dnd.ground.domain.user.User user = userRepository.findByNickname(principal.getUsername()).orElseThrow();
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        // 닉네임과 함께 response
        JSONObject json = new JSONObject();
        json.put("nickname", principal.getUsername());
        response.getWriter().write(String.valueOf(json));
    }
}
