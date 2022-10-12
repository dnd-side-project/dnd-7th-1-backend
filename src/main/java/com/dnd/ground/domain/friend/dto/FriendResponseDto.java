package com.dnd.ground.domain.friend.dto;

import com.dnd.ground.domain.challenge.dto.ChallengeResponseDto;
import com.dnd.ground.domain.friend.FriendStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 친구와 관련한 정보 조회용 Response DTO
 * @author  박찬호
 * @since   2022-08-02
 * @updated 1.친구 요청에 대한 응답 처리 결과 반환용 DTO 생성 (ResponseResult)
 *          - 2022.10.10 박찬호
 */

@Data @Builder
public class FriendResponseDto {

    @ApiModelProperty(value="친구 정보 리스트", example="[NickB, NickC]")
    private List<FInfo> infos;

    @ApiModelProperty(value="친구 수", example="3")
    private Integer size;

    
    //친구와 관련한 정보 모음
    @Data @NoArgsConstructor
    static public class FInfo {

        @Builder(builderMethodName = "of")
        public FInfo(String nickname, String picturePath) {
            this.nickname = nickname;
            this.picturePath = picturePath;
        }
        
        @ApiModelProperty(value="닉네임", example="NickA")
        private String nickname;

        @ApiModelProperty(value="프로필 사진 URI(카카오 프로필 사용 시 kakao/카카오회원번호)", example="http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg")
        private String picturePath;
    }

    /*회원 프로필 관련 DTO*/
    @Data
    @Builder
    static public class FriendProfile {
        @ApiModelProperty(value = "친구 닉네임", example = "NickA")
        private String nickname;

        @ApiModelProperty(value = "친구의 마지막 접속 시간", example = "2022-08-18T18:10:43.78")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime lasted;

        @ApiModelProperty(value = "친구의 소개 메시지", example = "친구의 소개 메시지 예시입니다.")
        private String intro;

        @ApiModelProperty(value = "회원과 친구 관계인지 나타내는 Boolean", example = "true")
        private Boolean isFriend;

        @ApiModelProperty(value = "이번 주 영역 개수", example = "9")
        private Long areas;

        @ApiModelProperty(value = "역대 누적 칸수", example = "1030")
        private Long allMatrixNumber;

        @ApiModelProperty(value = "역대 누적 랭킹", example = "1")
        private Integer rank;

        @ApiModelProperty(value = "회원과 함께 하는 챌린지 리스트"
                , example = "[{\"name\": \"챌린지1\", \"started\": \"2022-08-16\", \"ended\": \"2022-08-21\", \"rank\": 1, \"color\": \"Red\"}]")
        List<ChallengeResponseDto.Progress> challenges;

        @ApiModelProperty(value="프로필 사진 URI(카카오 프로필 사용 시 kakao/카카오회원번호)", example="http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg")
        private String picturePath;
    }

    /*친구 요청 수락, 거절 등에 대한 결과*/
    @Data @AllArgsConstructor
    static public class ResponseResult {
        @ApiModelProperty(value = "회원 닉네임(요청받은 사람)", example = "NickA")
        private String userNickname;

        @ApiModelProperty(value = "친구 닉네임(요청한 사람)", example = "NickB")
        private String friendNickname;

        @ApiModelProperty(value = "변경된 상태(결과: Accept, Reject)", example = "Accept")
        private FriendStatus status;
    }

}
