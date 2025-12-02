package io.github.hamsteak.trendlapse.global.elapsedtimelogging.config;

import io.github.hamsteak.trendlapse.global.elapsedtimelogging.aop.ElapsedTimeLoggingAdvice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Slf4j
@RequiredArgsConstructor
public class ElapsedTimeLoggingPostProcessor implements BeanPostProcessor {
    private final ElapsedTimeLoggingProperties elapsedTimeLoggingProperties;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        for (ElapsedTimeLoggingTask task : elapsedTimeLoggingProperties.getTasks()) {
            if (!task.isEnabled()) {
                continue;
            }

            DefaultPointcutAdvisor advisor = getAdvisorFromTask(task);

            Class<?> beanClass = AopProxyUtils.ultimateTargetClass(bean);
            if (!AopUtils.canApply(advisor.getPointcut(), beanClass)) {
                continue;
            }

            ProxyFactory proxyFactory = new ProxyFactory(bean);
            proxyFactory.setProxyTargetClass(true);
            proxyFactory.addAdvisor(advisor);
            Object proxy = proxyFactory.getProxy();

            log.info("Created elapsed time logging proxy for bean: {} of type: {}", beanName, beanClass.getName());

            bean = proxy;
        }

        return bean;
    }

    private static DefaultPointcutAdvisor getAdvisorFromTask(ElapsedTimeLoggingTask task) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(task.getPointcut());
        ElapsedTimeLoggingAdvice advice = new ElapsedTimeLoggingAdvice(task.getName());
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
        return advisor;
    }

}
