package com.dnd.ground.domain.user.dto;

import com.dnd.ground.domain.matrix.dto.MatrixDto;
import com.dnd.ground.domain.user.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @description 유저 Response Dto
 *              1. 유저(나) 매트릭스 및 정보
 *              2. 나와 챌린지 안하는 친구들 매트릭스
 *              3. 나와 챌린지 하는 친구들 매트릭스 및 정보
 *              4. 누적 칸의 수에 대한 랭킹 정보
 *  *           5. 누적 영역의 수에 대한 랭킹 정보
 * @author  박세헌, 박찬호
 * @since   2022-08-08
 * @updated 1. 접근 제어자 변경
 *          2. 회원의 누적 영역 컬럼 추가(UserMatrix.matricesNumber)
 *          3. 마지막 위치 반환을 위한 컬럼 추가(각 DTO에 latitude, longitude 추가)
 *          4. 일부 생성자, 수정자 및 어노테이션 변경
 *          - 2022.08.09 박찬호
 *          1. 칸 정보 모두 MatrixDto로 관리
 *          2. 영역 랭킹 어떻게 할지 고민 필요..
 *          - 2022.08.10 박세헌
 */

@Data
public class UserResponseDto {

    @Data
    static public class UserMatrix{
        @ApiModelProperty(value = "닉네임", example = "NickA", required = true)
        private String nickname;

        @ApiModelProperty(value = "현재 나의 영역", example = "77", required = true)
        private Integer matricesNumber;

        @ApiModelProperty(value = "유저의 마지막 위치 - 위도", example = "마지막 위치(위도)")
        private Double latitude;

        @ApiModelProperty(value = "유저의 마지막 위치 - 경도", example = "마지막 위치(경도)")
        private Double longitude;

        @ApiModelProperty(value = "칸 꼭지점 위도, 경도 리스트", required = true)
        private List<MatrixDto> matrices;

        //생성자
        public UserMatrix(User user) {
            this.nickname = user.getNickName();
            this.matricesNumber = 0;
            this.latitude = user.getLatitude();
            this.longitude = user.getLongitude();
        }

        //수정자 모음
        public void setProperties(String nickname, int matricesNumber, List<MatrixDto> matrices, Double lat, Double lon) {
            this.setNickname(nickname);
            this.setMatricesNumber(matricesNumber);
            this.setMatrices(matrices);
            this.setLatitude(lat);
            this.setLongitude(lon);
        }
    }

    @Data @AllArgsConstructor
    static public class FriendMatrix{
        @ApiModelProperty(value = "닉네임", example = "NickB", required = true)
        private String nickname;

        @ApiModelProperty(value = "친구의 마지막 위치 - 위도", example = "마지막 위치(위도)")
        private Double latitude;

        @ApiModelProperty(value = "친구의 마지막 위치 - 경도", example = "마지막 위치(경도)")
        private Double longitude;

        @ApiModelProperty(value = "칸 꼭지점 위도, 경도 리스트", required = true)
        private List<MatrixDto> matrices;

    }

    @Data
    @AllArgsConstructor
    static public class ChallengeMatrix{
        @ApiModelProperty(value = "닉네임", example = "NickC", required = true)
        private String nickname;

        @ApiModelProperty(value = "나와 같이 하는 챌린지 개수", example = "1", required = true)
        private Integer challengeNumber;

        @ApiModelProperty(value = "지도에 나타나는 챌린지 대표 색깔", example = "#ffffff", required = true)
        private String challengeColor;

        @ApiModelProperty(value = "챌린지를 같이 하는 사람의 마지막 위치 - 위도", example = "마지막 위치(위도)", required = true)
        private Double latitude;

        @ApiModelProperty(value = "챌린지를 같이 하는 사람의 마지막 위치 - 경도", example = "마지막 위치(경도)", required = true)
        private Double longitude;

        @ApiModelProperty(value = "칸 꼭지점 위도, 경도 리스트", required = true)
        private List<MatrixDto> matrices;
    }

    @Data
    @AllArgsConstructor
    public static class matrixRanking{
        @ApiModelProperty(value = "랭크", example = "1위", required = true)
        private Integer rank;

        @ApiModelProperty(value = "닉네임", example = "NickA", required = true)
        private String nickname;

        @ApiModelProperty(value = "누적 칸의 수", example = "누적 칸의 수", required = true)
        private Long matrixNumber;
    }

    @Data
    @AllArgsConstructor
    public static class areaRanking{
        @ApiModelProperty(value = "랭크", example = "1위", required = true)
        private Integer rank;

        @ApiModelProperty(value = "닉네임", example = "NickA", required = true)
        private String nickname;

        @ApiModelProperty(value = "누적 영역의 수", example = "누적 영역의 수", required = true)
        private Integer areaNumber;
    }
}
