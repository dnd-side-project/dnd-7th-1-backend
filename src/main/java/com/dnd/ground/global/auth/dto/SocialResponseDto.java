package com.dnd.ground.global.auth.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @description 소셜 로그인 후 발급된 토큰을 통해 회원 정보를 받아오는 DTO
 * @author  박찬호
 * @since   2023-01-20
 * @updated 1.DTO 생성
 *          - 2023-01-20 박찬호
 */

@Getter
@AllArgsConstructor
public class SocialResponseDto {
    @ApiModelProperty(value = "이메일", example = "lpv081900@naver.com")
    private String email;
    @ApiModelProperty(value="프로필 사진 경로 URI(카카오 프로필 사용 시 kakao/카카오회원번호)", example="http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg")
    private String picturePath;
    @ApiModelProperty(value="프로필 사진 이름", example="user/profile/default_profile.png")
    private String pictureName;
    @ApiModelProperty(value = "회원 가입 유무", example = "true")
    private boolean isSigned;

    /**
     * 카카오 로그인 Redirect URI 결과 Response
     * */
    @Getter
    @Setter
    public static class KakaoRedirectDto {
            public KakaoRedirectDto(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
        private String accessToken;
        private String refreshToken;
        private String email;
    }

    /**
     * 애플 로그인 Redirect URI의 Request DTO
     */
    @Getter
    @Setter
    public static class AppleLoginResponseDto {
        private String state;
        private String code;
        private String id_token;
        private String user;
    }
}