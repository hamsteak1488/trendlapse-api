package io.github.hamsteak.trendlapse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableRetry(proxyTargetClass = true)
@ConfigurationPropertiesScan
public class TrendlapseApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrendlapseApplication.class, args);
    }

}
