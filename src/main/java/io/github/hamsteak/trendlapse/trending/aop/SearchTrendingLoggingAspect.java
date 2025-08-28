package io.github.hamsteak.trendlapse.trending.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SearchTrendingLoggingAspect {
    @Around("execution(* io.github.hamsteak.trendlapse.trending.service.TrendingService.searchTrending(..))")
    public Object logSearchTrendingTime(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = joinPoint.proceed();

        stopWatch.stop();

        log.info("Finished searching trending task. (elapsed {}ms)", stopWatch.lastTaskInfo().getTimeMillis());

        return result;
    }
}
