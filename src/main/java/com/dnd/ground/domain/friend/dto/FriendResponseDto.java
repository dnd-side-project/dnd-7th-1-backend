package com.dnd.ground.domain.friend.dto;

import com.dnd.ground.domain.challenge.dto.ChallengeResponseDto;
import com.dnd.ground.domain.friend.FriendStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 친구와 관련한 정보 조회용 Response DTO
 * @author  박찬호
 * @since   2022-08-02
 * @updated 1.결과 확인을 위해 FInfo 클래스 toString 생성
 *          - 2023.05.27 박찬호
 */

@Getter
@AllArgsConstructor
@Builder
public class FriendResponseDto {

    @ApiModelProperty(value="친구 정보 리스트", example="[NickB, NickC]")
    private List<FInfo> infos;

    @ApiModelProperty(value="친구 수", example="3")
    private Integer size;

    @ApiModelProperty(value="마지막 페이지 여부", example = "true")
    private Boolean isLast;

    @ApiModelProperty(value="다음 페이지에 필요한 ID", example = "4")
    private Long offset;

    @Getter
    @AllArgsConstructor
    public static class FInfo {
        @ApiModelProperty(value="닉네임", example="NickA")
        private String nickname;

        @ApiModelProperty(value="프로필 사진 URI(카카오 프로필 사용 시 kakao/카카오회원번호)", example="http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg")
        private String picturePath;

        @Override
        public String toString() {
            return "FInfo{" +
                    "nickname='" + nickname + '\'' +
                    ", picturePath='" + picturePath + '\'' +
                    '}';
        }
    }

    /*회원 프로필 관련 DTO*/
    @Getter
    @Builder
    public static class FriendProfile {
        @ApiModelProperty(value = "친구 닉네임", example = "NickA")
        private String nickname;

        @ApiModelProperty(value = "친구의 마지막 접속 시간", example = "2022-08-18T18:10:43.78")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime lasted;

        @ApiModelProperty(value = "친구의 소개 메시지", example = "친구의 소개 메시지 예시입니다.")
        private String intro;

        @ApiModelProperty(value = "회원과 친구 관계에 대한 정보\nAccept: 친구\nRequesting: 친구 요청중\nResponseWait: 수락 대기중\nNoFriend: 친구 아님", example = "RequestWait")
        private FriendStatus isFriend;

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
    @AllArgsConstructor
    @Getter
    public static class ResponseResult {
        @ApiModelProperty(value = "회원 닉네임(요청하는 사람)", example = "NickA")
        private String userNickname;

        @ApiModelProperty(value = "친구 닉네임(요청받는 사람)", example = "NickB")
        private String friendNickname;

        @ApiModelProperty(value = "변경된 상태(결과: Accept, Reject)", example = "Accept")
        private FriendStatus status;
    }

    @AllArgsConstructor
    @Getter
    public static class RecommendResponse {
        @ApiModelProperty(value="친구 정보 리스트", example="[NickB, NickC]")
        private List<FInfo> infos;

        @ApiModelProperty(value="친구 수", example="3")
        private Integer size;

        @ApiModelProperty(value="마지막 페이지 여부", example = "true")
        private Boolean isLast;

        @ApiModelProperty(value="다음 페이지에 필요한 거리 (해당 거리 이상 회원 조회)", example = "92831.2342")
        private Double offset;
    }

}
