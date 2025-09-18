package io.github.hamsteak.trendlapse.collector.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Aspect
@Component
@ConditionalOnProperty(name = "only-korea-region", havingValue = "true")
public class CollectOnlyKoreaAspect {
    @Around("execution(* io.github.hamsteak.trendlapse.collector.domain.TrendingCollector.collect(..))")
    public Object logApiCallTime(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        args[2] = List.of("KR");
        return joinPoint.proceed(args);
    }
}
