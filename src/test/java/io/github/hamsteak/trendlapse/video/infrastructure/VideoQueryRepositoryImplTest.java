package io.github.hamsteak.trendlapse.video.infrastructure;

import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.channel.domain.ChannelRepository;
import io.github.hamsteak.trendlapse.video.application.dto.VideoSearchFilter;
import io.github.hamsteak.trendlapse.video.application.dto.VideoView;
import io.github.hamsteak.trendlapse.video.domain.Video;
import io.github.hamsteak.trendlapse.video.domain.VideoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(VideoQueryRepositoryImplTest.QueryDslRepositoryConfig.class)
class VideoQueryRepositoryImplTest {
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    VideoRepository videoRepository;
    @Autowired
    VideoQueryRepositoryImpl videoQueryRepositoryImpl;

    Channel channel;

    @BeforeEach
    void setUp() {
        channel = channelRepository.saveAndFlush(Channel.builder()
                .youtubeId("Channel Youtube ID")
                .title("Channel Title")
                .build());
        IntStream.rangeClosed(1, 5)
                .forEach(i -> videoRepository.saveAndFlush(Video.builder()
                        .channelId(channel.getId())
                        .youtubeId("Video Youtube ID " + i)
                        .title("Video Title " + (i < 3 ? "Less than three" : "Greater equal than three"))
                        .thumbnailUrl("Video ThumbnailUrl " + i)
                        .build()));
    }

    @ParameterizedTest
    @MethodSource("conditionResultArgumentProviders")
    void search_returns_VideoViewPage_when_condition_met(VideoSearchFilter filter, int expectedTotal) {
        // given
        Pageable pageable = Pageable.ofSize(3);

        // when
        PagedModel<VideoView> videoViewPage = videoQueryRepositoryImpl.search(filter, pageable);

        // then
        assertThat(videoViewPage.getMetadata().totalElements()).isEqualTo(expectedTotal);
    }

    static Stream<Arguments> conditionResultArgumentProviders() {
        return Stream.of(
                Arguments.of(
                        VideoSearchFilter.builder()
                                .youtubeId(null)
                                .title(null)
                                .channelTitle(null)
                                .build(),
                        0),
                Arguments.of(
                        VideoSearchFilter.builder()
                                .youtubeId("Video Youtube ID 2")
                                .title(null)
                                .channelTitle(null)
                                .build(),
                        1
                ),
                Arguments.of(
                        VideoSearchFilter.builder()
                                .youtubeId(null)
                                .title("Less")
                                .channelTitle(null)
                                .build(),
                        2
                ),
                Arguments.of(
                        VideoSearchFilter.builder()
                                .youtubeId(null)
                                .title("Greater equal")
                                .channelTitle(null)
                                .build(),
                        3
                )
        );
    }

    @Test
    void search_returns_valid_VideoViewPage() {
        // given
        Pageable pageable = Pageable.ofSize(3);
        VideoSearchFilter filter = VideoSearchFilter.builder()
                .youtubeId(null)
                .title(null)
                .channelTitle(channel.getTitle())
                .build();

        // when
        PagedModel<VideoView> videoViewPage = videoQueryRepositoryImpl.search(filter, pageable);

        // then
        assertThat(videoViewPage.getMetadata().totalElements()).isEqualTo(5);
        assertThat(videoViewPage.getMetadata().totalPages()).isEqualTo(2);
        assertThat(videoViewPage.getMetadata().size()).isEqualTo(3);
        assertThat(videoViewPage.getMetadata().number()).isEqualTo(0);
    }

    @TestConfiguration
    public static class QueryDslRepositoryConfig {
        @Bean
        public VideoQueryRepositoryImpl videoQueryRepositoryImpl(EntityManager entityManager) {
            return new VideoQueryRepositoryImpl(entityManager);
        }
    }
}