package com.dnd.ground.domain.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @description 카카오 API를 사용할 때 필요한 DTO
 * @author  박찬호
 * @since   2022-08-23
 * @updated 1. DTO 생성
 *          - 2022.08.23 박찬호
 */

@Data
public class KakaoDto {

    /*카카오에게 받은 토큰*/
    @Data
    public static class Token {
        @ApiModelProperty(value="카카오로부터 받은 엑세스 토큰", example="vXezXAmm8Yj2frWVLofx_4v4c9EOwfaAokojr1c2Cj11mgAAAYLKLqJr")
        private String access_token;

        @ApiModelProperty(value="카카오로부터 받은 리프레시 토큰", example="2LqQd2jnW50rHbOyGyyKu_xNRv4p2Jri7wWsso7RCj11mgAAAYLKLqJq")
        private String refresh_token;
    }

    /*토큰 정보*/
    @Data
    public static class TokenInfo {
        @ApiModelProperty(value="카카오 회원 번호(우리 회원 번호X)", example="2399961704")
        private Long id;

        @ApiModelProperty(value="토큰 만료까지 남은 시간(단위:초)", example="21599")
        private Integer expires_in;

        @ApiModelProperty(value="카카오에서의 우리 서비스 번호", example="788508")
        private Integer app_id;
    }

    @Data
    public static class UserInfo {
        @ApiModelProperty(value="카카오 회원 번호(우리 회원 번호X)", example="2399961704")
        private Long id;

        @ApiModelProperty(value="이메일(테스트 안해본 상태)", example="koc081900@naver.com")
        private String email;

        @ApiModelProperty(value="사용자 정보(프로필 사진 관련)", example="{\"profile_image\":\"http://k.kakaocdn.net/dn/3O7st/btrHep5Sa9R/4lxH3JhkeZyqK0KFB1XoGk/img_640x640.jpg\",\"thumbnail_image\":\"http://k.kakaocdn.net/dn/3O7st/btrHep5Sa9R/4lxH3JhkeZyqK0KFB1XoGk/img_110x110.jpg\"}")
        private Map<String, String> properties;
    }
}
