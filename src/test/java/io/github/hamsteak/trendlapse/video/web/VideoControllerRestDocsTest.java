package io.github.hamsteak.trendlapse.video.web;

import io.github.hamsteak.trendlapse.test.support.RestDocsTestSupport;
import io.github.hamsteak.trendlapse.video.application.SearchVideoService;
import io.github.hamsteak.trendlapse.video.application.dto.SearchVideoCommand;
import io.github.hamsteak.trendlapse.video.application.dto.VideoView;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VideoController.class)
class VideoControllerRestDocsTest extends RestDocsTestSupport {
    @MockitoBean
    SearchVideoService searchVideoService;

    @Test
    void search_restdocs() throws Exception {
        // given
        long id = 1L;
        long channelId = 1L;
        String youtubeId = "Video Youtube ID";
        String title = "Video Title";
        String thumbnailUrl = "Video Thumbnail Url";

        int pageSize = 5;
        int pageNumber = 1;
        int totalElmenets = 6;

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        when(searchVideoService.search(any(SearchVideoCommand.class), any(Pageable.class)))
                .thenReturn(new PagedModel<>(new PageImpl<>(
                        List.of(new VideoView(id, channelId, youtubeId, title, thumbnailUrl)),
                        pageRequest,
                        totalElmenets
                )));

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/videos")
                        .queryParam("channelId", String.valueOf(channelId))
                        .queryParam("youtubeId", youtubeId)
                        .queryParam("title", title)
                        .queryParam("size", String.valueOf(pageSize))
                        .queryParam("page", String.valueOf(pageNumber))
        ).andExpect(status().isOk());

        // then
        ArgumentCaptor<SearchVideoCommand> cmdCaptor = ArgumentCaptor.forClass(SearchVideoCommand.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(searchVideoService).search(cmdCaptor.capture(), pageableCaptor.capture());
        assertThat(cmdCaptor.getValue().getChannelId()).isEqualTo(channelId);
        assertThat(cmdCaptor.getValue().getYoutubeId()).isEqualTo(youtubeId);
        assertThat(cmdCaptor.getValue().getTitle()).isEqualTo(title);
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(pageSize);
        assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(pageNumber);

        // docs
        resultActions
                .andDo(document(
                        "video/search",
                        queryParameters(
                                parameterWithName("channelId").description("Channel ID")
                                        .attributes(
                                                key("constraints").value("-"),
                                                key("optional").value("true")
                                        ),
                                parameterWithName("youtubeId").description("Youtube ID")
                                        .attributes(
                                                key("constraints").value("-"),
                                                key("optional").value("true")
                                        ),
                                parameterWithName("title").description("Title")
                                        .attributes(
                                                key("constraints").value("-"),
                                                key("optional").value("true")
                                        ),
                                parameterWithName("size").description("Page Size")
                                        .attributes(
                                                key("constraints").value("-"),
                                                key("optional").value("true")
                                        ),
                                parameterWithName("page").description("Page Number (0-based)")
                                        .attributes(
                                                key("constraints").value("-"),
                                                key("optional").value("true")
                                        )
                        ),
                        responseFields(
                                fieldWithPath("content").description("Content")
                                        .attributes(
                                                key("constraints").value("-"),
                                                key("optional").value("true")
                                        ),
                                fieldWithPath("content[].id").description("Video ID")
                                        .attributes(
                                                key("constraints").value("-"),
                                                key("optional").value("true")
                                        ),
                                fieldWithPath("content[].channelId").description("Channel ID")
                                        .attributes(
                                                key("constraints").value("-"),
                                                key("optional").value("true")
                                        ),
                                fieldWithPath("content[].youtubeId").description("YouTube video ID")
                                        .attributes(
                                                key("constraints").value("-"),
                                                key("optional").value("true")
                                        ),
                                fieldWithPath("content[].title").description("Video title")
                                        .attributes(
                                                key("constraints").value("-"),
                                                key("optional").value("true")
                                        ),
                                fieldWithPath("content[].thumbnailUrl").description("Thumbnail URL")
                                        .attributes(
                                                key("constraints").value("-"),
                                                key("optional").value("true")
                                        ),

                                fieldWithPath("page").description("Paging information")
                                        .attributes(
                                                key("constraints").value("-"),
                                                key("optional").value("true")
                                        ),
                                fieldWithPath("page.size").description("Page size")
                                        .attributes(
                                                key("constraints").value("-"),
                                                key("optional").value("true")
                                        ),
                                fieldWithPath("page.number").description("Current page number (0-based)")
                                        .attributes(
                                                key("constraints").value("-"),
                                                key("optional").value("true")
                                        ),
                                fieldWithPath("page.totalElements").description("Total number of elements")
                                        .attributes(
                                                key("constraints").value("-"),
                                                key("optional").value("true")
                                        ),
                                fieldWithPath("page.totalPages").description("Total number of pages")
                                        .attributes(
                                                key("constraints").value("-"),
                                                key("optional").value("true")
                                        )
                        )
                ));
    }
}
