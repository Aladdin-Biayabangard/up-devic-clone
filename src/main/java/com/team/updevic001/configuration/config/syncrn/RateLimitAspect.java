package com.team.updevic001.configuration.config.syncrn;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RateLimitAspect {

    private static final int MAX_REQUESTS = 500; // Maksimum sorğu sayı
    private static final long TIME_WINDOW = 2000; // 1 dəqiqə
    private long lastRequestTime = 0;
    private int requestCount = 0;

    @Before("@annotation(RateLimit)")
    public void checkRateLimit() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastRequestTime > TIME_WINDOW) {
            lastRequestTime = currentTime;
            requestCount = 0;
        }

        requestCount++;

        if (requestCount > MAX_REQUESTS) {
            throw new RuntimeException("Sorğu limiti aşılmışdır!");
        }
    }
}
