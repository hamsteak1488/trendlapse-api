package io.github.hamsteak.trendlapse.region.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hamsteak.trendlapse.region.application.dto.RegionDetail;
import io.github.hamsteak.trendlapse.region.application.service.RegionService;
import io.github.hamsteak.trendlapse.support.fixture.DomainFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(RegionController.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.trendlapse.com", uriPort = 443)
class RegionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RegionService regionService;

    @Test
    @DisplayName("GET /regions - 지역 목록을 조회한다")
    void shouldReturnRegionDetails() throws Exception {
        // given
        List<RegionDetail> mockRegionDetails = List.of(
                DomainFixture.createRegionDetail("KR"),
                DomainFixture.createRegionDetail("US"),
                DomainFixture.createRegionDetail("JP")
        );
        given(regionService.getRegionDetails()).willReturn(mockRegionDetails);

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders
                        .get("/regions")
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then & docs
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(mockRegionDetails)))
                .andDo(document("get-regions",
                        responseFields(
                                fieldWithPath("[].regionCode").type(JsonFieldType.STRING).description("지역 코드")
                                        .attributes(
                                                key("constraints").value("none"),
                                                key("optional").value("false")
                                        ),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("지역 이름")
                                        .attributes(
                                                key("constraints").value("none"),
                                                key("optional").value("false")
                                        )
                        )
                ));
    }
}

