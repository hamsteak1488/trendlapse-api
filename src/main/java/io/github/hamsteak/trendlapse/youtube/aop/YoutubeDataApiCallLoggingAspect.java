package io.github.hamsteak.trendlapse.youtube.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Mono;

@Slf4j
@Aspect
@Component
@ConditionalOnProperty(prefix = "youtube-data-api", name = "use-log", havingValue = "true")
public class YoutubeDataApiCallLoggingAspect {
    @Pointcut("execution(* io.github.hamsteak.trendlapse.youtube.infrastructure.YoutubeDataApiCaller.*(..))")
    private void blockingYoutubeDataApiCaller() {
    }

    @Pointcut("execution(reactor.core.publisher.Mono io.github.hamsteak.trendlapse.youtube.infrastructure.NonblockingYoutubeDataApiCaller.*(..))")
    private void nonblockingYoutubeDataApiCaller() {
    }

    @Around("blockingYoutubeDataApiCaller()")
    public Object logApiCallTime(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = joinPoint.proceed();

        stopWatch.stop();
        String methodName = getMethodName(joinPoint);
        log.debug("API Call:{}, elapsed {}ms", methodName, stopWatch.lastTaskInfo().getTimeMillis());

        return result;
    }

    @Around("nonblockingYoutubeDataApiCaller()")
    public Object logNonblockingApiCallTime(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        if (!(result instanceof Mono<?> monoResult)) {
            return result;
        }

        String methodName = getMethodName(joinPoint);

        return Mono.defer(() -> {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            return monoResult.doFinally(signalType -> {
                stopWatch.stop();
                log.debug("API Call:{}, elapsed {}ms", methodName, stopWatch.lastTaskInfo().getTimeMillis());
            });
        });
    }

    private static String getMethodName(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod().getName();
    }
}
