package com.dnd.ground.global.config;

import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.TimeZone;

/**
 * @description 스프링부트 톰캣 설정 파일
 * @author  박세헌, 박찬호
 * @since   2022-09-12
 * @updated 1. 특수문자 처리
 *          2. 타임존 설정(Asia/Seoul)
 *          - 2022-09-12 박세헌
 */

@Configuration
public class TomcatConfig
        implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addConnectorCustomizers((TomcatConnectorCustomizer)
                connector -> connector.setProperty("relaxedQueryChars", "<>[\\]^`{|}"));
    }

    @PostConstruct
    public void start() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}