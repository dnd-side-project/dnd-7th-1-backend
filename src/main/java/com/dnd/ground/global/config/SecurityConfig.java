package com.dnd.ground.global.config;

import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.auth.filter.JWTFilter;
import com.dnd.ground.global.auth.filter.JWTReissueFilter;
import com.dnd.ground.global.auth.filter.SignFilter;
import com.dnd.ground.global.auth.service.AppleService;
import com.dnd.ground.global.auth.service.AuthService;
import com.dnd.ground.global.auth.service.KakaoService;
import com.dnd.ground.global.auth.filter.*;
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
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


/**
 * @description 스프링 시큐리티 config 클래스
 * @author  박찬호
 * @since   2022-08-24
 * @updated 1. 필터 리팩토링에 따른 설정 변경
 *          - 2023-01-30 박찬호
 */

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final AuthService authService;
    private final KakaoService kakaoService;
    private final AppleService appleService;
    private final UserRepository userRepository;
    private final AuthenticationEntryPoint authenticationEntryPoint;

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
        SignFilter signFilter = new SignFilter(authenticationManager(authenticationConfiguration), authService, authenticationEntryPoint);
        JWTFilter checkFilter = new JWTFilter(authenticationManager(authenticationConfiguration), authService, authenticationEntryPoint);
        JWTReissueFilter reissueFilter = new JWTReissueFilter(authenticationManager(authenticationConfiguration), kakaoService, authService, authenticationEntryPoint);
        LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration), userRepository, kakaoService, appleService, authenticationEntryPoint);

        http
                .csrf().disable()
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(checkFilter, BasicAuthenticationFilter.class)
                .addFilterBefore(reissueFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(signFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint);
        return http.build();
    }

    /* 스프링 시큐리티 룰을 무시하게 하는 Url 규칙(여기 등록하면 규칙 적용하지 않음) */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .antMatchers("/doc", "/swagger*/**", "/favicon*/**", "/v2/api-docs")
                .antMatchers("/login", "/sign");
    }

}