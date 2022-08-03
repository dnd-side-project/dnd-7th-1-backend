package com.dnd.ground.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.*;

/**
 * @description 홈화면 구성 Response Dto
 *              1. 유저 매트릭스 및 정보
 *              2. 챌린지 안하는 친구들 매트릭스
 *              3. 챌린지 하는 친구들 매트릭스 및 정보
 * @author  박세헌
 * @since   2022-08-02
 * @updated 2022-08-02 / 생성 : 박세헌
 */

@Data @Builder
public class HomeResponseDto {
    private UserMatrix userMatrix;
    private List<FriendMatrix> friendMatrices = new ArrayList<>();
    private List<ChallengeMatrix> challengeMatrices = new ArrayList<>();

    @AllArgsConstructor
    static public class UserMatrix{
        public String nickname;
        public Set<ShowMatrix> matrices = new HashSet<>();
    }

    @AllArgsConstructor
    static public class FriendMatrix{
        public Set<ShowMatrix> matrices;
    }

    @AllArgsConstructor
    static public class ChallengeMatrix{
        public String nickname;
        public Set<ShowMatrix> matrices = new HashSet<>();
        public String challengeColor;
    }

    // 중복 제거
    @Builder
    static public class ShowMatrix{
        public Double latitude;
        public Double longitude;

        @Override
        public int hashCode() {
            return Objects.hash(latitude, longitude);
        }

        @Override
        public boolean equals(Object obj) {
            if (this.getClass() != obj.getClass()) return false;
            return (Objects.equals(((ShowMatrix) obj).latitude, this.latitude)) &&
                    (Objects.equals(((ShowMatrix) obj).longitude, this.longitude));
        }
    }
}
