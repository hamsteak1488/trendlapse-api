package io.github.hamsteak.trendlapse.member.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hamsteak.trendlapse.member.application.LoginService;
import io.github.hamsteak.trendlapse.member.application.dto.LoginRequest;
import io.github.hamsteak.trendlapse.test.support.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerRestDocsTest extends RestDocsTestSupport {
    @MockitoBean
    LoginService loginService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void login_restdocs() throws Exception {
        LoginRequest request = new LoginRequest("Steve", "1234");
        when(loginService.login(any())).thenReturn(1L);

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andDo(document(
                        "auth/login",
                        requestFields(
                                fieldWithPath("username").description("Username")
                                        .attributes(
                                                key("constraints").value("none"),
                                                key("optional").value("false")
                                        ),
                                fieldWithPath("password").description("Password")
                                        .attributes(
                                                key("constraints").value("none"),
                                                key("optional").value("false")
                                        )
                        )
                ));
    }

    @Test
    void logout_restdocs() throws Exception {
        mockMvc.perform(
                        post("/auth/logout")
                                .contentType(MediaType.ALL)
                )
                .andExpect(status().isOk())
                .andDo(document(
                        "auth/logout"
                ));
    }
}
