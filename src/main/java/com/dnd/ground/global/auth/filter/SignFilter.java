/**
 * @description
 * 인증되지 않은 회원은 Servlet에 진입하지 못하게 하려는 의도로 회원가입 필터를 구현했음.
 * 하지만, 다음과 같은 이유로 의도한대로 동작하지 않아 우선 서비스 계층으로 이동.
 * 1. AuthService.class 의 signUp 메소드를 수행하고 나오면, 트랜잭션이 끝남에 따라 DB에 엔티티가 저장됨.
 * 2. UsernamePasswordAuthenticationFilter을 상속받음에 따라 회원은 인증을 거쳐야 함.
 * 3. 트랜잭션이 DB에 커밋되어 엔티티가 반영되기 전 인증을 시도하여 실패하는 일이 종종 발생하는 문제가 발생.
 *
 * 추가적으로 필터 레벨에서 회원가입을 처리하게 되면, 문제가 발생했을 때 추적하기 위한 추가적인 예외 처리가 필요하고, 따로 로그를 남기는 등의 오버헤드가 발생한다.
 * @deprecated
 */
//package com.dnd.ground.global.auth.filter;
//
//import com.dnd.ground.domain.user.repository.UserRepository;
//import com.dnd.ground.global.auth.UserClaim;
//import com.dnd.ground.global.auth.dto.UserSignDto;
//import com.dnd.ground.global.auth.service.AuthService;
//import com.dnd.ground.global.exception.AuthException;
//import com.dnd.ground.global.exception.ExceptionCodeSet;
//import com.dnd.ground.global.exception.FilterException;
//import com.dnd.ground.global.exception.FriendException;
//import com.dnd.ground.global.util.JwtUtil;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.http.HttpHeaders;
//import org.json.JSONObject;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * @description 회원가입을 처리하는 필터.
// *              필터 레벨에서 처리하고, 서블릿 컨테이너로 접근하지 않도록 함.
// * @author  박찬호
// * @since   2023-01-23
// * @updated 1. 새로운 필터 생성
// *          - 2023.01.23 박찬호
// */
//
//public class SignFilter extends UsernamePasswordAuthenticationFilter {
//
//    public SignFilter(AuthenticationManager authenticationManager,
//                      AuthService authService,
//                      UserRepository userRepository,
//                      AuthenticationEntryPoint authenticationEntryPoint) {
//        super(authenticationManager);
//        this.authService = authService;
//        this.userRepository = userRepository;
//        this.authenticationEntryPoint = authenticationEntryPoint;
//        setFilterProcessesUrl("/sign");
//    }
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//    private final AuthService authService;
//    private final UserRepository userRepository;
//    private final AuthenticationEntryPoint authenticationEntryPoint;
//    private UserClaim claim;
//    private static final String BEARER = "Bearer ";
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request,
//                                                HttpServletResponse response) throws AuthenticationException {
//
//        UsernamePasswordAuthenticationToken authenticationToken;
//        try {
//            UserSignDto userSignDto = objectMapper.readValue(request.getInputStream(), UserSignDto.class);
//            this.claim = authService.signUp(userSignDto);
//
//            long createdMilli = UserClaim.changeCreatedToLong(claim.getCreated());
//            authenticationToken = new UsernamePasswordAuthenticationToken(
//                    claim.getEmail(), claim.getNickname() + "-" + createdMilli
//            );
//        } catch (IOException e) {
//            throw new FilterException(ExceptionCodeSet.INTERNAL_SERVER_ERROR);
//        } catch (AuthException | FriendException e) {
//            throw new FilterException(e.getExceptionCode());
//        }
//
//        return getAuthenticationManager().authenticate(authenticationToken);
//    }
//
//    @Override
//    protected void successfulAuthentication(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain chain,
//            Authentication authResult) throws IOException {
//        String accessToken = JwtUtil.createAccessToken(claim.getEmail(), claim.getCreated());
//        String refreshToken = JwtUtil.createRefreshToken(claim.getEmail(), claim.getCreated());
//
//        response.setHeader(HttpHeaders.AUTHORIZATION, BEARER + accessToken);
//        response.setHeader("Refresh-Token", BEARER + refreshToken);
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//
//        JSONObject json = new JSONObject();
//        json.put("nickname", claim.getNickname());
//        response.getWriter().write(String.valueOf(json));
//    }
//
//    @Override
//    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
//                                              AuthenticationException failed) throws IOException, ServletException {
//        if (claim != null && userRepository.findByEmail(claim.getEmail()).isPresent()) {
//            userRepository.deleteByEmail(claim.getEmail());
//        }
//
//        SecurityContextHolder.clearContext();
//        authenticationEntryPoint.commence(request, response, failed);
//    }
//}