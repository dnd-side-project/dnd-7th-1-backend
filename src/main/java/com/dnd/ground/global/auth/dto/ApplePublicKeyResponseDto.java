package com.dnd.ground.global.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

/**
 * @description 애플의 idToken 검증을 위한 Public key model
 * @author  박찬호
 * @since   2023-01-18
 * @updated 1.public key model 생성 및 kid가 일치하는 객체 반환 메소드 생성
 *          - 2023.01.20 박찬호
 */

@Getter
@Setter
public class ApplePublicKeyResponseDto {
    private List<Key> keys;

    @Getter
    @Setter
    public static class Key {
        private String kty;
        private String kid;
        private String use;
        private String alg;
        private String n;
        private String e;
    }

    /**
     * kid가 일치하는 object 반환
     */
    public Optional<Key> getMatchedKey(String kid, String alg) {
        return this.keys.stream()
                .filter(key -> key.getKid().equals(kid) && key.getAlg().equals(alg))
                .findFirst();
    }
}
