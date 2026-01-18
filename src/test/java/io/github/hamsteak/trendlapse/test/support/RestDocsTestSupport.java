package io.github.hamsteak.trendlapse.test.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hamsteak.trendlapse.config.RestDocsConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@ExtendWith(RestDocumentationExtension.class)
@Import(RestDocsConfig.class)
public abstract class RestDocsTestSupport {
    /*
        setUp 메서드 인수 주입은 JUnit이 담당하지만 RestDocsMockMvcConfigurationCustomizer는
        Spring Container에 의해 관리되고 있으므로 @AutoWired를 통해 주입해야함.
    */
    @Autowired
    RestDocsMockMvcConfigurationCustomizer customizer;
    @Autowired
    protected ObjectMapper objectMapper;

    protected MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        MockMvcRestDocumentationConfigurer configurer = documentationConfiguration(restDocumentation);
        customizer.customize(configurer);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(configurer)
                .build();
    }
}
