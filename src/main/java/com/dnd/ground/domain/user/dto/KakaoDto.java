package com.dnd.ground.domain.user.dto;

import com.dnd.ground.domain.friend.FriendStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 카카오 API를 사용할 때 필요한 DTO
 * @author  박찬호
 * @since   2022-08-23
 * @updated 1.친구 목록 조회 관련 DTO 수정
 *          - 2022.01.12 박찬호
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

    @Builder
    @Data
    public static class UserInfo {
        @ApiModelProperty(value="카카오 회원 번호(우리 회원 번호X)", example="2399961704")
        private Long id;

        @ApiModelProperty(value="카카오 이메일", example="koc081900@naver.com")
        private String email;

        @ApiModelProperty(value="프로필 사진 이름(카카오 프로필 사용 시 kakao/카카오회원번호)", example="kakao/2399961704")
        private String pictureName;

        @ApiModelProperty(value="프로필 사진 URI(카카오 프로필 사용 시 kakao/카카오회원번호)", example="http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg")
        private String picturePath;
    }

    //카카오 친구 목록 조회 결과
    @Data
    public static class FriendsInfoFromKakao {
        private List<KakaoFriend> elements;
        private Integer total_count;
        private String before_url;
        private String after_url;
        private Integer favorite_count;

        //각 카카오 친구의 정보
        @Data
        public static class KakaoFriend {
            private Long id;
            private String uuid;
            private Boolean favorite;
            private String profile_nickname;
            private String profile_thumbnail_image;
        }
    }

    @Data
    @NoArgsConstructor
    public static class kakaoFriendResponse {
        @ApiModelProperty(value="다음 친구 목록 요청시 필요한 offset", example="16")
        private Integer nextOffset;

        @ApiModelProperty(value="이번 페이지의 친구 개수(몇명인지)", example="16")
        private Integer size;

        @ApiModelProperty(value="마지막 페이지인지(true이면 다음 offset은 0)", example="true")
        private Boolean isLast;

        @ApiModelProperty(value="친구 정보 리스트", example="[{'nickname':'NickA', 'kakaoName':'박찬호','status':'accept','picturePath':'http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg'}]")
        private List<FriendsInfo> friendsInfo = new ArrayList<>();

        @Data
        @Builder
        public static class FriendsInfo {
            @ApiModelProperty(value="네모두에서의 닉네임", example="NickA")
            private String nickname;

            @ApiModelProperty(value="카카오톡에서의 이름", example="박찬호")
            private String kakaoName;

            @ApiModelProperty(value="나와 카카오친구와의 관계(Accept:친구, Requesting:요청중, ResponseWait:응답대기중, NoFriend:친구아님)", example="Accept")
            private FriendStatus status;

            @ApiModelProperty(value="프로필 사진 URI(카카오 프로필 사용 시 kakao/카카오회원번호)", example="http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg")
            private String picturePath;
        }
    }
}
