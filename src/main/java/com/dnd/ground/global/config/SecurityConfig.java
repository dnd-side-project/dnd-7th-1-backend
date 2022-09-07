package com.dnd.ground.global.config;

import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.domain.user.service.AuthService;
import com.dnd.ground.domain.user.service.UserService;
import com.dnd.ground.global.securityFilter.JWTCheckFilter;
import com.dnd.ground.global.securityFilter.JWTSignFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


/**
 * @description 스프링 시큐리티 config 클래스
 * @author  박세헌
 * @since   2022-08-24
 * @updated 1. 생성
 *          - 2022-08-24 박세헌
 */

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final AuthService authService;
    private final UserRepository userRepository;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 패스워드는 BCryptPasswordEncoder로 보안
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 회원가입 or 재로그인 인증 필터
        JWTSignFilter loginFilter = new JWTSignFilter(authenticationManager(authenticationConfiguration), authService, userRepository);
        // 매 request마다 토큰을 검사 해주는 필터
        JWTCheckFilter checkFilter = new JWTCheckFilter(authenticationManager(authenticationConfiguration), authService, userRepository);

        http
                .csrf().disable()  // csrf x
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // 세션 x
                )
                .authorizeRequests()
                .antMatchers("/", "/sign", "/auth/kakao/login").permitAll()  // 누구나 접근 가능
                .anyRequest().authenticated() // 나머지 요청들은 권한의 종류에 상관 없이 권한이 있어야 접근 가능
                .and()
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(checkFilter, BasicAuthenticationFilter.class);

        return http.build();
    }

}