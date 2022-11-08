package com.dnd.ground.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.List;

/**
 * @description Swagger 설정 파일
 * @author  박찬호
 * @since   2022-07-18
 * @updated JWT 토큰 인증 관련 설정 추가
 *          2022-09-14 박찬호
 */

@Configuration
public class SwaggerConfig {
    private String version = "V0.1";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(securityJWT(), kakaoJWT()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("DND 7th team.1")
                .description("DND 7th team.1 API document")
                .version(version)
                .build();
    }

    private ApiKey securityJWT() {
        return new ApiKey("ACCESS-TOKEN", "Authorization", "header");
    }

    private ApiKey kakaoJWT() {
        return new ApiKey("KAKAO-ACCESS-TOKEN", "Kakao-Access-Token", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEveryThing");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;

        return Arrays.asList(
                new SecurityReference("ACCESS-TOKEN", authorizationScopes),
                new SecurityReference("KAKAO-ACCESS-TOKEN", authorizationScopes)
        );
    }
}
