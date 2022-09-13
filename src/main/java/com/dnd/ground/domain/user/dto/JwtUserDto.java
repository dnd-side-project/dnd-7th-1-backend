package com.dnd.ground.domain.user.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 카카오 정보를 받기 위한 dto
 * @author  박세헌
 * @since   2022-08-24
 * @updated 1. api 명세 추가 - 2022-09-13 박세헌
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtUserDto {

    @ApiModelProperty(value="카카오 회원 번호(우리 회원 번호X)", example="2399961704")
    private Long id;  // 카카오 id

    @ApiModelProperty(value=" 회원 닉네임", example="NickA")
    private String nickname;

    @ApiModelProperty(value="카카오 이메일", example="koc081900@naver.com")
    private String mail;

    @ApiModelProperty(value="프로필 사진 이름(카카오 프로필 사용 시 kakao/카카오회원번호)", example="kakao/2399961704")
    private String pictureName;

    @ApiModelProperty(value="프로필 사진 URI(카카오 프로필 사용 시 kakao/카카오회원번호)", example="http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg")
    private String picturePath;

    @ApiModelProperty(value="카카오로부터 받은 리프레시 토큰", example="2LqQd2jnW50rHbOyGyyKu_xNRv4p2Jri7wWsso7RCj11mgAAAYLKLqJq")
    private String kakaoRefreshToken;
}