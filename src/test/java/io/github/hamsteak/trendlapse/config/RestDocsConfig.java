package io.github.hamsteak.trendlapse.config;

import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

@TestConfiguration
public class RestDocsConfig {
    @Bean
    public RestDocsMockMvcConfigurationCustomizer mockMvcConfigurationCustomizer() {
        return configurer -> configurer
                .operationPreprocessors()
                .withRequestDefaults(
                        prettyPrint(),
                        modifyUris().scheme("https").host("api.trendlapse.com").removePort(),
                        modifyHeaders().remove("Content-Length")
                )
                .withResponseDefaults(
                        prettyPrint(),
                        modifyHeaders().remove("Vary")
                )
                ;
    }
}