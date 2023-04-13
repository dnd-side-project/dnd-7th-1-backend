package com.dnd.ground.global.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @description 빈이 아닌 클래스에서 빈을 활용하기 위한 ApplicationProvider
 * @author 박찬호
 * @since 2023-04-10
 * @updated 1. 클래스 생성
 *          - 2023.04.10 박찬호
 */

@Component
public class ApplicationContextProvider implements ApplicationContextAware {
    private static ApplicationContext context;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static Object getBean(Class<?> classType) {
        return context.getBean(classType);
    }
}
