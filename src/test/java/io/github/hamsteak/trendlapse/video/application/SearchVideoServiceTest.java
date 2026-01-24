package io.github.hamsteak.trendlapse.video.application;

import io.github.hamsteak.trendlapse.video.application.dto.SearchVideoCommand;
import io.github.hamsteak.trendlapse.video.application.dto.VideoSearchFilter;
import io.github.hamsteak.trendlapse.video.application.dto.VideoView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchVideoServiceTest {
    @Mock
    VideoQueryRepository videoQueryRepository;
    @InjectMocks
    SearchVideoService searchVideoService;

    @Test
    void search_returns_VideoViewPage() {
        // given
        long id = 1L;
        long channelId = 10L;
        String youtubeId = "Video Youtube ID";
        String title = "Video Title";
        String thumbnailUrl = "Video Thumbnail Url";

        SearchVideoCommand command = new SearchVideoCommand(channelId, youtubeId, title);
        VideoSearchFilter filter = new VideoSearchFilter(channelId, youtubeId, title);
        Pageable pageable = Pageable.ofSize(10);

        when(videoQueryRepository.search(filter, pageable))
                .thenReturn(new PagedModel<>(new PageImpl<>(List.of(new VideoView(id, channelId, youtubeId, title, thumbnailUrl)))));

        // when
        PagedModel<VideoView> videoViewPage = searchVideoService.search(command, pageable);

        // then
        assertThat(videoViewPage.getMetadata().totalElements()).isEqualTo(1);
        assertThat(videoViewPage.getContent().get(0).getId()).isEqualTo(id);
        assertThat(videoViewPage.getContent().get(0).getChannelId()).isEqualTo(channelId);
        assertThat(videoViewPage.getContent().get(0).getYoutubeId()).isEqualTo(youtubeId);
        assertThat(videoViewPage.getContent().get(0).getTitle()).isEqualTo(title);
        assertThat(videoViewPage.getContent().get(0).getThumbnailUrl()).isEqualTo(thumbnailUrl);
    }
}