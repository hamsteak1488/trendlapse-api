package io.github.hamsteak.trendlapse.global.querycounter;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class QueryCounterDataSourceProxyPostProcessor implements BeanPostProcessor {
    private final ObjectProvider<MeterRegistry> meterRegistryProvider;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSource originalDataSource) {
            QueryExecutionListener listener = new CounterQueryExecutionListener(meterRegistryProvider);

            ProxyDataSource queryCounterDS = ProxyDataSourceBuilder
                    .create(originalDataSource)
                    .name("QueryCounterDS")
                    .listener(listener)
                    .build();

            Class<?> beanClass = AopProxyUtils.ultimateTargetClass(bean);
            log.info("Created query counter proxy for bean: {} of type: {}", beanName, beanClass.getName());

            return queryCounterDS;
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    /*
     * Micrometer Counter를 지연 초기화하기 위한 리스너.
     *
     * - ObjectProvider 사용 이유:
     *   MeterRegistry가 DataSource 프록시 적용 시점에 아직 초기화되지 않았을 수 있어,
     *   실제 필요 시점에 lazy하게 안전하게 가져오기 위함.
     *
     * - volatile 사용 이유:
     *   싱글톤 환경에서 Counter가 여러 스레드에 동시에 부분 초기화 상태로 노출되는 것을 방지하고,
     *   정상적인 가시성과 메모리 재정렬 방지를 보장하기 위해 필요.
     *
     * - 동시성 고려:
     *   Counter는 한 번만 초기화되고 이후에는 안전하게 재사용되어야 하므로,
     *   멀티스레드 환경에서도 단일 인스턴스 생성과 가시성을 보장하는 구조가 필요하다.
     */
    @RequiredArgsConstructor
    private static class CounterQueryExecutionListener implements QueryExecutionListener {
        private final ObjectProvider<MeterRegistry> meterRegistryProvider;
        private volatile Counter counter;

        @Override
        public void beforeQuery(ExecutionInfo executionInfo, List<QueryInfo> list) {

        }

        @Override
        public void afterQuery(ExecutionInfo executionInfo, List<QueryInfo> list) {
            if (counter == null) {
                MeterRegistry meterRegistry = meterRegistryProvider.getIfAvailable();

                if (meterRegistry == null) {
                    return;
                }

                synchronized (this) {
                    if (counter == null) {
                        counter = Counter.builder("query.count")
                                .description("Counts the number of SELECT query executions.")
                                .baseUnit("queries")
                                .register(meterRegistry);
                    }
                }
            }

            counter.increment(list.size());
        }
    }
}
