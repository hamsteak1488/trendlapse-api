package io.github.hamsteak.trendlapse.global.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ElapsedTimeLoggingPostProcessor implements BeanPostProcessor {
    private final List<ElapsedTimeLoggingTask> tasks;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        for (ElapsedTimeLoggingTask task : tasks) {
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

            log.info("Created proxy for bean: {} of type: {}", beanName, beanClass.getName());

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
