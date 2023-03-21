package com.dnd.ground.global.aop;

import com.dnd.ground.global.log.CommonLogger;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @description Audit log를 남기기 위한 컨트롤러 AOP
 * @author 박찬호
 * @since 2023-03-08
 * @updated 1.Audit Log 기록을 위한 클래스 생성
 *          - 2023.03.08 박찬호
 */

@Aspect
@Component
@Slf4j
public class ControllerAuditAop {
    private final CommonLogger logger;

    public ControllerAuditAop(@Qualifier("auditLogger") CommonLogger logger) {
        this.logger = logger;
    }

    @Around("execution(public * com.dnd.ground.domain.*.controller.*Controller.*(..))")
    public Object requestLogging(ProceedingJoinPoint joint) throws Throwable {
        long start = System.currentTimeMillis();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        Map<String, Object> auditLogData = new HashMap<>();
        auditLogData.put("requestTime", LocalDateTime.now());
        auditLogData.put("uri", request.getRequestURI());
        auditLogData.put("param", getParam(request, joint));
        try {
            Object result = joint.proceed(joint.getArgs());
            auditLogData.put("runningTime", System.currentTimeMillis() - start);

            JSONObject json = new JSONObject(auditLogData);
            logger.write(json.toJSONString().replaceAll(" ",""));
            return result;
        } catch (Exception e) {
            auditLogData.put("runningTime", System.currentTimeMillis() - start);
            JSONObject json = new JSONObject(auditLogData);
            logger.errorWrite(json.toJSONString().replaceAll(" ",""));
            throw e;
        }
    }

    private Map<String, Object> getParam(HttpServletRequest request, ProceedingJoinPoint joint) {
        Map<String, Object> response = new HashMap<>();
        if (request.getMethod().equals("GET")) {
            for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                response.put(entry.getKey(), Arrays.toString(entry.getValue()));
            }
        } else {
            MethodSignature signature = (MethodSignature) joint.getSignature();
            String[] parameterNames = signature.getParameterNames();
            Object[] args = joint.getArgs();

            for(int i=0; i<args.length; i++) {
                try {
                    response.put(parameterNames[i], args[i]);
                } catch (UnsupportedOperationException | ClassCastException |
                         NullPointerException | IllegalArgumentException | IndexOutOfBoundsException  e) {
                    log.error("전달되는 파라미터 정보가 올바르지 않습니다.");
                }
            }
        }
        return response;
    }
}
