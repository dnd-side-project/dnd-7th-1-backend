package com.dnd.ground.domain.friend.dto;

import com.dnd.ground.domain.challenge.dto.ChallengeResponseDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 친구와 관련한 정보 조회용 Response DTO
 * @author  박찬호
 * @since   2022-08-02
 * @updated 1. Profile 클래스 이동(UserResponseDto -> FriendResponseDto) 및 이름 변경(Profile -> FriendProfile)
 *          - 2022.08.26 박찬호
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
        public FInfo(String nickname) {
            this.nickname = nickname;
        }
        
        @ApiModelProperty(value="닉네임", example="NickA")
        private String nickname;
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
    }

}
