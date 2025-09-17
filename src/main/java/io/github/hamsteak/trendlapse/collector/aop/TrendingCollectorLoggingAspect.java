package io.github.hamsteak.trendlapse.collector.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
@ConditionalOnProperty(prefix = "collector", name = "use-log", havingValue = "true")
public class TrendingCollectorLoggingAspect {
    @Around("execution(* io.github.hamsteak.trendlapse.collector.domain.TrendingCollector.collect(..))")
    public Object logApiCallTime(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Started trendings collection task.");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = joinPoint.proceed();

        stopWatch.stop();

        log.info("Finished trendings collection task. (elapsed {}ms)", stopWatch.lastTaskInfo().getTimeMillis());

        return result;
    }
}
