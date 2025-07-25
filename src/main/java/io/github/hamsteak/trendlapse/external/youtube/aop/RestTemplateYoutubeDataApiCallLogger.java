package io.github.hamsteak.trendlapse.external.youtube.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RestTemplateYoutubeDataApiCallLogger {
    @Before("execution(* io.github.hamsteak.trendlapse.external.youtube.infrastructure.RestTemplateYoutubeDataApiCaller.*(..))")
    public void logBeforeCall(JoinPoint joinPoint) {
        log.info("External API:({}) is called", joinPoint.getSignature().toShortString());
    }
}
