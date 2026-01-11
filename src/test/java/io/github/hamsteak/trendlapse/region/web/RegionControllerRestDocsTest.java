package io.github.hamsteak.trendlapse.region.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hamsteak.trendlapse.region.application.dto.RegionView;
import io.github.hamsteak.trendlapse.region.application.service.GetRegionViewService;
import io.github.hamsteak.trendlapse.test.support.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegionController.class)
class RegionControllerRestDocsTest extends RestDocsTestSupport {
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private GetRegionViewService getRegionViewService;

    @Test
    void getRegions_restdocs() throws Exception {
        List<RegionView> regionViews = List.of(
                regionView("KR", "South Korea"),
                regionView("US", "United States"),
                regionView("JP", "Japan")
        );
        given(getRegionViewService.getRegionViews()).willReturn(regionViews);

        // docs
        mockMvc.perform(
                        get("/regions")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(regionViews)))
                .andDo(document("get-regions",
                        responseFields(
                                fieldWithPath("[].regionId").type(JsonFieldType.STRING).description("지역 코드")
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

    private RegionView regionView(String regionId, String name) {
        return RegionView.builder()
                .regionId(regionId)
                .name(name)
                .build();
    }
}

