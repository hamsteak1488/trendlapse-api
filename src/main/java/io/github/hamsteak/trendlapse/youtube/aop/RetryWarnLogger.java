package io.github.hamsteak.trendlapse.youtube.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RetryWarnLogger implements RetryListener {
    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        log.warn("Retry #{} for {} failed (exception={})", context.getRetryCount(), context.getAttribute(RetryContext.NAME), throwable.toString());
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        if (throwable != null) {
            log.error("All {} attempts exhausted for {} — giving up", context.getRetryCount() + 1, context.getAttribute(RetryContext.NAME), throwable);
        }
    }
}
