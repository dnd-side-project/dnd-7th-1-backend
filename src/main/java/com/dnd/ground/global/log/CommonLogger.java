package com.dnd.ground.global.log;

/**
 * @description 로깅을 위한 역할 정의 인터페이스
 * @author 박찬호
 * @since 2023-03-08
 * @updated 1.일반적인 로깅(DEBUG-INFO) 메소드 생성(write)
 *          2.에러 로깅(ERROR,FATAL) 메소드 생성(errorWrite)
 *          - 2023.03.08 박찬호
 */
public interface CommonLogger {
    void write(String log);
    void errorWrite(String log);
}
