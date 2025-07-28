package io.github.hamsteak.trendlapse.external.youtube.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
@ConditionalOnProperty(prefix = "youtube-data-api", name = "use-log", havingValue = "true")
public class YoutubeDataApiCallLogger {
    @Before("execution(* io.github.hamsteak.trendlapse.external.youtube.infrastructure.RestTemplateYoutubeDataApiCaller.*(..))")
    public void logBeforeApiCall(JoinPoint joinPoint) {
        String methodName = getMethodName(joinPoint);

//        log.info("Called External API: {}", methodName);
    }

    @Around("execution(* io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller.*(..))")
    public Object logApiCallTime(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = joinPoint.proceed();

        stopWatch.stop();
        String methodName = getMethodName(joinPoint);
        log.info("task:{}, elapsed {}ms", methodName, stopWatch.lastTaskInfo().getTimeMillis());

        return result;
    }

    private static String getMethodName(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod().getName();
    }
}
