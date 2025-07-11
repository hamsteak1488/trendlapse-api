package io.github.hamsteak.youtubetimelapse.external.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RestTemplateYoutubeDataApiCallLogger {
    @Before("execution(* io.github.hamsteak.youtubetimelapse.external.youtube.RestTemplateYoutubeDataApiCaller.*(..))")
    public void logBeforeCall(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        log.info("External API:({}) is called", methodSignature.getMethod().getName());
    }
}
