package io.github.hamsteak.trendlapse.global.logelapsedtime.config;

import io.github.hamsteak.trendlapse.global.logelapsedtime.aop.ElapsedTimeLoggingAdvice;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.List;

@Slf4j
public class ElapsedTimeLoggingPostProcessor implements BeanPostProcessor {
    private final List<Task> tasks;

    public ElapsedTimeLoggingPostProcessor(ElapsedTimeLoggingProperties elapsedTimeLoggingProperties) {
        tasks = elapsedTimeLoggingProperties.getTasks().stream()
                .filter(ElapsedTimeLoggingTask::isEnabled)
                .map(task -> {
                    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                    pointcut.setExpression(task.getPointcut());
                    return new Task(pointcut, task.getName());
                })
                .toList();
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        for (Task task : tasks) {
            Class<?> beanClass = AopProxyUtils.ultimateTargetClass(bean);
            if (!AopUtils.canApply(task.getPointcut(), beanClass)) {
                continue;
            }

            ProxyFactory proxyFactory = new ProxyFactory(bean);
            proxyFactory.setProxyTargetClass(true);
            proxyFactory.addAdvisor(getAdvisorFromTask(task, beanName));
            Object proxy = proxyFactory.getProxy();

            log.info("Created elapsed time logging proxy for bean: {} of type: {}", beanName, beanClass.getName());

            bean = proxy;
        }

        return bean;
    }

    private static DefaultPointcutAdvisor getAdvisorFromTask(Task task, String beanName) {
        ElapsedTimeLoggingAdvice advice = new ElapsedTimeLoggingAdvice(task.getName(), beanName);
        return new DefaultPointcutAdvisor(task.getPointcut(), advice);
    }

    @Getter
    @RequiredArgsConstructor
    private static class Task {
        private final Pointcut pointcut;
        private final String name;
    }
}
