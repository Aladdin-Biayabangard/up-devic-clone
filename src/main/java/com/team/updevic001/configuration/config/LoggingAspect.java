package com.team.updevic001.configuration.config;

import com.team.updevic001.dao.entities.User;
import com.team.updevic001.utility.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private final AuthHelper authHelper;
    private final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.team.updevic001.services..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Long userId = getUserIdSafe();

        logger.info("ActionLog.{}.START{}", methodName, userId != null ? " id: " + userId : "");

        try {
            Object result = joinPoint.proceed();
            logger.info("ActionLog.{}.SUCCESS{}", methodName, userId != null ? " id: " + userId : "");
            return result;
        } catch (Exception ex) {
            logger.error("ActionLog.{}.FAILED{} - Error: {}", methodName,
                    userId != null ? " id: " + userId : "", ex.getMessage(), ex);
            throw ex;
        }
    }

    private Long getUserIdSafe() {
        try {
            User user = authHelper.getAuthenticatedUser();
            return user.getId();
        } catch (Exception e) {
            return null;
        }
    }
}

