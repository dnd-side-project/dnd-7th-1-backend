package com.dnd.ground.global.config;

import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.domain.user.service.AuthService;
import com.dnd.ground.domain.user.service.KakaoService;
import com.dnd.ground.global.exception.CustomAuthenticationEntryPoint;
import com.dnd.ground.global.securityFilter.JWTCheckFilter;
import com.dnd.ground.global.securityFilter.JWTLoginFilter;
import com.dnd.ground.global.securityFilter.JWTSignFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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
 * @updated 1. 로그인 필터 추가
 *          2. 기존 SignFilter의 지역 변수명 변경 (loginFilter -> signFilter)
 *          - 2022-09-25 박찬호
 */

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final KakaoService kakaoService;

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
        JWTSignFilter signFilter = new JWTSignFilter(authenticationManager(authenticationConfiguration), authService, userRepository);
        // 매 request마다 토큰을 검사 해주는 필터
        JWTCheckFilter checkFilter = new JWTCheckFilter(authenticationManager(authenticationConfiguration), authService, userRepository);
        // 로그인 필터
        JWTLoginFilter loginFIlter = new JWTLoginFilter(authenticationManager(authenticationConfiguration), kakaoService, userRepository);

        http
                .csrf().disable()  // csrf x
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // 세션 x
                )
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(signFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(loginFIlter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(checkFilter, BasicAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());
        return http.build();
    }

    /* 스프링 시큐리티 룰을 무시하게 하는 Url 규칙(여기 등록하면 규칙 적용하지 않음) */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .antMatchers("/doc", "/swagger*/**", "/favicon*/**", "/v2/api-docs")
                .antMatchers("/auth/signup", "/auth/check/origin", "/auth/check/nickname", "/auth/kakao/login");
    }

}