package com.dnd.ground.domain.challenge.dto;

import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

public class UCDto {

    //챌린지에 참가하는 멤버들의 정보
    @Data
    public static class UCInfo {
        @ApiModelProperty(value="주최자의 프로필 사진 URI(카카오 프로필 사용 시 kakao/카카오회원번호)", example="http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg")
        private String picturePath;

        @ApiModelProperty(value = "닉네임", example = "NickA")
        private String nickname;

        @ApiModelProperty(value = "챌린지 준비 상태 여부", example = "Wait")
        private ChallengeStatus status;

        @QueryProjection
        public UCInfo(String picturePath, String nickname, ChallengeStatus status) {
            this.picturePath = picturePath;
            this.nickname = nickname;
            this.status = status;
        }
    }

    public static class ProgressCount {

    }
}
