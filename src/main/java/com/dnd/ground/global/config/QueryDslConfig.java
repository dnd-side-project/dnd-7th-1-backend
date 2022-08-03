package com.dnd.ground.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @description queryDsl 설정 파일
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-01 / JPAQueryFactory 스프링 빈 등록 : 박세헌
 */

@Configuration
public class QueryDslConfig {
    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}