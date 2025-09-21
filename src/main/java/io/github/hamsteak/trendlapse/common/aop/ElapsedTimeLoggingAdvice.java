package io.github.hamsteak.trendlapse.common.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.StopWatch;

@Slf4j
@RequiredArgsConstructor
public class ElapsedTimeLoggingAdvice implements MethodInterceptor {
    private final String taskName;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = invocation.proceed();

        stopWatch.stop();
        log.debug("Elapsed {}ms for task:[{}] completion.", stopWatch.lastTaskInfo().getTimeMillis(), taskName);

        return result;
    }
}
