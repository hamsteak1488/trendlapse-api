package io.github.hamsteak.trendlapse.youtube.aop;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class NonblockingApiCallerTimedAspect {
    private final MeterRegistry meterRegistry;

    @Pointcut("execution(reactor.core.publisher.Mono io.github.hamsteak.trendlapse.youtube.domain.NonblockingYoutubeDataApiCaller.*(..))")
    private void nonblockingYoutubeDataApiCaller() {
    }

    @Around("nonblockingYoutubeDataApiCaller()")
    public Object measureTimeOfNonblockingApiCaller(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getName();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();

        Object result = joinPoint.proceed();
        if (!(result instanceof Mono<?> monoResult)) {
            return result;
        }

        Timer timer = findOrCreateTimer(className, methodName);

        return Mono.defer(() -> {
            Timer.Sample sample = Timer.start();
            return monoResult.doFinally(signalType -> sample.stop(timer));
        });
    }

    private Timer findOrCreateTimer(String className, String methodName) {
        Timer timer = meterRegistry.find("youtube.api.call")
                .tag("class", className)
                .tag("method", methodName)
                .tag("exception", "none")
                .timer();

        if (timer == null) {
            timer = Timer.builder("youtube.api.call")
                    .tag("class", className)
                    .tag("method", methodName)
                    .tag("exception", "none")
                    .register(meterRegistry);
        }

        return timer;
    }

}
