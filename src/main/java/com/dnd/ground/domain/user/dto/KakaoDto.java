package com.dnd.ground.domain.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * @description 카카오 API를 사용할 때 필요한 DTO
 * @author  박찬호
 * @since   2022-08-23
 * @updated 1.카카오 메시지 API 관련 DTO 생성
 *          - 2022.05.19 박찬호
 */

public class KakaoDto {

    /*카카오에게 받은 토큰*/
    @Getter
    public static class Token {
        @ApiModelProperty(value="카카오로부터 받은 엑세스 토큰", example="vXezXAmm8Yj2frWVLofx_4v4c9EOwfaAokojr1c2Cj11mgAAAYLKLqJr")
        private String access_token;

        @ApiModelProperty(value="카카오로부터 받은 리프레시 토큰", example="2LqQd2jnW50rHbOyGyyKu_xNRv4p2Jri7wWsso7RCj11mgAAAYLKLqJq")
        private String refresh_token;
    }

    /*토큰 정보*/
    @Getter
    public static class TokenInfo {
        @ApiModelProperty(value="카카오 회원 번호(우리 회원 번호X)", example="2399961704")
        private Long id;

        @ApiModelProperty(value="토큰 만료까지 남은 시간(단위:초)", example="21599")
        private Integer expires_in;

        @ApiModelProperty(value="카카오에서의 우리 서비스 번호", example="788508")
        private Integer app_id;
    }

    @AllArgsConstructor
    @Builder
    @Getter
    public static class UserInfo {
        @ApiModelProperty(value="카카오 회원 번호(우리 회원 번호X)", example="2399961704")
        private Long kakaoId;

        @ApiModelProperty(value="카카오 이메일", example="koc081900@naver.com")
        private String email;

        @ApiModelProperty(value="프로필 사진 이름(카카오 프로필 사용 시 kakao/카카오회원번호)", example="kakao/2399961704")
        private String pictureName;

        @ApiModelProperty(value="프로필 사진 URI(카카오 프로필 사용 시 kakao/카카오회원번호)", example="http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg")
        private String picturePath;
    }

    //카카오 친구 목록 조회 결과
    @Getter
    public static class FriendsInfoFromKakao {
        private List<KakaoFriendElement> elements;
        private Integer total_count;
        private String before_url;
        private String after_url;
        private Integer favorite_count;

        //각 카카오 친구의 정보
        @Getter
        public static class KakaoFriendElement {
            private Long id;
            private String uuid;
            private Boolean favorite;
            private String profile_nickname;
            private String profile_thumbnail_image;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class KakaoFriendResponse {
        @ApiModelProperty(value="마지막 페이지인지 여부", example="true")
        private Boolean isLast;
        private List<KakaoFriend> friends;

        @ApiModelProperty(value="다음 친구 목록 요청시 필요한 offset", example="16")
        private Integer offset;

        @AllArgsConstructor
        @Getter
        public static class KakaoFriend {
            @ApiModelProperty(value="카카오에서 사용하는 UUID(메시지 API에서 필요)", example="8765456543")
            private String uuid;

            @ApiModelProperty(value="카카오톡에서의 이름", example="박찬호")
            private String kakaoNickname;

            @ApiModelProperty(value="네모두에서의 닉네임", example="NickA")
            private String nickname;

            @ApiModelProperty(value="네모두 회원 여부", example="true")
            private Boolean isSigned;

            @ApiModelProperty(value="프로필 사진(없거나 기본 프로필의 경우 네모두 URL)", example="http://nemodu~")
            private String picturePath;
        }
    }

    /*토큰 재발급 DTO*/
    @Getter
    @Setter
    public static class ReissueToken {
        private String access_token;
        private String token_type;
        private String refresh_token;
        private Long refresh_token_expires_in;
        private Long expires_in;
    }

    /*카카오 메시지 API DTO*/
    @Getter
    @NoArgsConstructor
    public static class SendMessage {
        private String[] successful_receiver_uuids;
        private FailureInfo[] failure_info;

        @Getter
        @NoArgsConstructor
        public static class FailureInfo {
            private Integer code;
            private String msg;
            private String[] receiver_uuids;
        }
    }

    /*카카오 예외 클래*/
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class KakaoExceptionDto {
        Integer code;
        String msg;
    }
}
