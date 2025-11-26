package io.github.hamsteak.trendlapse.trending.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hamsteak.trendlapse.support.fixture.DomainFixture;
import io.github.hamsteak.trendlapse.trending.application.dto.DateTimeTrendingDetailList;
import io.github.hamsteak.trendlapse.trending.application.service.TrendingService;
import io.github.hamsteak.trendlapse.trending.web.dto.GetTrendingRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrendingController.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.trendlapse.com", uriPort = 443)
class TrendingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrendingService trendingService;

    @Test
    @DisplayName("GET /trendings - 트렌딩 목록을 조회한다")
    void shouldReturnTrendingDetails() throws Exception {
        // given
        String regionCode = "KR";
        ZonedDateTime startDateTime = ZonedDateTime.parse("2025-01-01T00:00:00Z");
        ZonedDateTime endDateTime = ZonedDateTime.parse("2025-01-01T23:59:59Z");

        List<DateTimeTrendingDetailList> mockTrendingDetails = List.of(
                DomainFixture.createDateTimeTrendingDetailList(regionCode, 1),
                DomainFixture.createDateTimeTrendingDetailList(regionCode, 2)
        );

        given(trendingService.searchTrending(any())).willReturn(mockTrendingDetails);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/trendings")
                        .param("regionCode", regionCode)
                        .param("startDateTime", startDateTime.toString())
                        .param("endDateTime", endDateTime.toString())
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then & docs
        ConstraintDescriptions constraintDescriptions = new ConstraintDescriptions(GetTrendingRequest.class);

        List<String> regionCodeDescription = constraintDescriptions.descriptionsForProperty("regionCode");
        List<String> startDateTimeDescription = constraintDescriptions.descriptionsForProperty("startDateTime");
        List<String> endDateTimeDescription = constraintDescriptions.descriptionsForProperty("endDateTime");

        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(mockTrendingDetails)))
                .andDo(document("get-trendings",
                        queryParameters(
                                parameterWithName("regionCode").description("지역 코드 (ISO 3166-1 alpha-2)")
                                        .attributes(key("constraints").value(regionCodeDescription), key("optional").value("false")),
                                parameterWithName("startDateTime").description("조회 시작 일시 (ISO 8601 형식, UTC)")
                                        .attributes(key("constraints").value(startDateTimeDescription), key("optional").value("false")),
                                parameterWithName("endDateTime").description("조회 종료 일시 (ISO 8601 형식, UTC)")
                                        .attributes(key("constraints").value(endDateTimeDescription), key("optional").value("false"))
                        ),
                        responseFields(
                                fieldWithPath("[].dateTime").description("트렌딩 데이터 수집 일시 (ISO 8601 형식)")
                                        .attributes(key("constraints").value(""), key("optional").value("false")),
                                fieldWithPath("[].items").description("트렌딩 상세 목록")
                                        .attributes(key("constraints").value(""), key("optional").value("false")),
                                fieldWithPath("[].items[].dateTime").description("트렌딩 상세 일시")
                                        .attributes(key("constraints").value(""), key("optional").value("false")),
                                fieldWithPath("[].items[].rank").description("트렌딩 순위")
                                        .attributes(key("constraints").value(""), key("optional").value("false")),
                                fieldWithPath("[].items[].videoDetail").description("비디오 상세 정보")
                                        .attributes(key("constraints").value(""), key("optional").value("false")),
                                fieldWithPath("[].items[].videoDetail.id").description("비디오 ID")
                                        .attributes(key("constraints").value(""), key("optional").value("false")),
                                fieldWithPath("[].items[].videoDetail.channelId").description("채널 ID")
                                        .attributes(key("constraints").value(""), key("optional").value("false")),
                                fieldWithPath("[].items[].videoDetail.youtubeId").description("유튜브 비디오 ID")
                                        .attributes(key("constraints").value(""), key("optional").value("false")),
                                fieldWithPath("[].items[].videoDetail.title").description("비디오 제목")
                                        .attributes(key("constraints").value(""), key("optional").value("false")),
                                fieldWithPath("[].items[].videoDetail.thumbnailUrl").description("비디오 썸네일 URL")
                                        .attributes(key("constraints").value(""), key("optional").value("false")),
                                fieldWithPath("[].items[].channelDetail").description("채널 상세 정보")
                                        .attributes(key("constraints").value(""), key("optional").value("false")),
                                fieldWithPath("[].items[].channelDetail.id").description("채널 ID")
                                        .attributes(key("constraints").value(""), key("optional").value("false")),
                                fieldWithPath("[].items[].channelDetail.youtubeId").description("유튜브 채널 ID")
                                        .attributes(key("constraints").value(""), key("optional").value("false")),
                                fieldWithPath("[].items[].channelDetail.title").description("채널 제목")
                                        .attributes(key("constraints").value(""), key("optional").value("false")),
                                fieldWithPath("[].items[].channelDetail.thumbnailUrl").description("채널 썸네일 URL")
                                        .attributes(key("constraints").value(""), key("optional").value("false"))
                        )
                ));
    }
}
