package io.github.hamsteak.trendlapse.collector.domain;

import io.github.hamsteak.trendlapse.channel.domain.ChannelCreator;
import io.github.hamsteak.trendlapse.channel.domain.ChannelFinder;
import io.github.hamsteak.trendlapse.collector.domain.v0.OneByOneChannelCollector;
import io.github.hamsteak.trendlapse.collector.domain.v1.BatchChannelCollector;
import io.github.hamsteak.trendlapse.external.youtube.dto.*;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChannelCollectorTest {
    @EqualsAndHashCode
    @RequiredArgsConstructor
    private static class Channel {
        final String youtubeId;
        final String title;
        final String thumbnailUrl;
    }

    private interface ChannelCollectorFactory {
        ChannelCollector create(Fixture fix);
    }

    @Builder
    private static class Fixture {
        final YoutubeDataApiProperties youtubeDataApiProperties;
        final YoutubeDataApiCaller youtubeDataApiCaller;
        final ChannelFinder channelFinder;
        final ChannelCreator channelCreator;
    }

    private static Stream<Named<ChannelCollectorFactory>> implementations() {
        return Stream.of(
                Named.of("OneByOneChannelCollector",
                        (fix) -> new OneByOneChannelCollector(
                                fix.youtubeDataApiCaller,
                                fix.channelFinder,
                                fix.channelCreator
                        )
                ),
                Named.of("BatchChannelCollector",
                        (fix) -> new BatchChannelCollector(
                                fix.youtubeDataApiProperties,
                                fix.youtubeDataApiCaller,
                                fix.channelFinder,
                                fix.channelCreator
                        )
                )
        );
    }

    @Builder
    @RequiredArgsConstructor
    private static class Case {
        final String name;
        final int maxResultCount;
        final List<String> requestChannelYoutubeIds;
        final List<String> existingChannelYoutubeIds;
        final List<Channel> expectedCreatedChannels;
    }

    private static Stream<Case> cases() {
        return Stream.of(
                Case.builder()
                        .name("한 개의 데이터")
                        .maxResultCount(50)
                        .requestChannelYoutubeIds(List.of("A"))
                        .existingChannelYoutubeIds(List.of())
                        .expectedCreatedChannels(List.of(makeChannel("A")))
                        .build(),
                Case.builder()
                        .name("여러 개의 데이터")
                        .maxResultCount(50)
                        .requestChannelYoutubeIds(List.of("A", "B", "C"))
                        .existingChannelYoutubeIds(List.of())
                        .expectedCreatedChannels(List.of(makeChannel("A"), makeChannel("B"), makeChannel("C")))
                        .build(),
                Case.builder()
                        .name("DB에 없는 데이터만 DB에 저장하는지 검사")
                        .maxResultCount(50)
                        .requestChannelYoutubeIds(List.of("A", "B", "C"))
                        .existingChannelYoutubeIds(List.of("B"))
                        .expectedCreatedChannels(List.of(makeChannel("A"), makeChannel("C")))
                        .build(),
                Case.builder()
                        .name("DB에 이미 모든 데이터가 존재")
                        .maxResultCount(50)
                        .requestChannelYoutubeIds(List.of("A", "B"))
                        .existingChannelYoutubeIds(List.of("A", "B"))
                        .expectedCreatedChannels(List.of())
                        .build(),
                Case.builder()
                        .name("중복 제거 후 저장하는지 검사")
                        .maxResultCount(50)
                        .requestChannelYoutubeIds(List.of("A", "A", "B", "B"))
                        .existingChannelYoutubeIds(List.of())
                        .expectedCreatedChannels(List.of(makeChannel("A"), makeChannel("B")))
                        .build(),
                Case.builder()
                        .name("MaxResultCount가 요청 데이터 목록 길이보다 작은 경우에도 정상 동작하는지 검사.")
                        .maxResultCount(2)
                        .requestChannelYoutubeIds(List.of("A", "B", "C", "D", "E"))
                        .existingChannelYoutubeIds(List.of())
                        .expectedCreatedChannels(List.of(makeChannel("A"), makeChannel("B"), makeChannel("C"), makeChannel("D"), makeChannel("E")))
                        .build(),
                Case.builder()
                        .name("MaxResultCount가 요청 데이터 목록 길이보다 작은 경우에도 정상 동작하는지 검사.")
                        .maxResultCount(2)
                        .requestChannelYoutubeIds(List.of("A", "B", "C", "D", "E"))
                        .existingChannelYoutubeIds(List.of())
                        .expectedCreatedChannels(List.of(makeChannel("A"), makeChannel("B"), makeChannel("C"), makeChannel("D"), makeChannel("E")))
                        .build()
        );
    }

    private static Stream<Arguments> params() {
        return implementations().flatMap(impl ->
                cases().map(tc ->
                        Arguments.arguments(impl.getName(), impl.getPayload(), tc.name, tc)
                )
        );
    }

    @ParameterizedTest(name = "({0}) x ({2})")
    @MethodSource("params")
    @DisplayName("모든 ChannelCollector 구현체에 대해 검증")
    void create_expected_items_from_api_items(String implName, ChannelCollectorFactory channelCollectorFactory, String tcName, Case tc) {
        // given
        ChannelFinder channelFinder = mock(ChannelFinder.class);
        ChannelCreator channelCreator = mock(ChannelCreator.class);
        YoutubeDataApiProperties youtubeDataApiProperties = new YoutubeDataApiProperties("baseUrl", "apiKey", tc.maxResultCount, false, 3);
        YoutubeDataApiCaller youtubeDataApiCaller = new ChannelMockYoutubeApiCaller();

        when(channelFinder.findMissingChannelYoutubeIds(anyList()))
                .then(invocationOnMock -> {
                    List<String> channelYoutubeIds = invocationOnMock.getArgument(0);
                    return channelYoutubeIds.stream()
                            .filter(channelYoutubeId -> !tc.existingChannelYoutubeIds.contains(channelYoutubeId)).toList();
                });

        Fixture fix = Fixture.builder()
                .youtubeDataApiProperties(youtubeDataApiProperties)
                .youtubeDataApiCaller(youtubeDataApiCaller)
                .channelFinder(channelFinder)
                .channelCreator(channelCreator)
                .build();

        ChannelCollector channelCollector = channelCollectorFactory.create(fix);

        // when
        int collectedCount = channelCollector.collect(tc.requestChannelYoutubeIds);

        // then
        ArgumentCaptor<String> youtubeIdArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> titleArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> thumbnailUrlArgumentCaptor = ArgumentCaptor.forClass(String.class);

        // Create 횟수가 기대되는 Create Youtube ID 목록 길이와 일치하는지 검사.
        verify(channelCreator, times(tc.expectedCreatedChannels.size())).create(
                youtubeIdArgumentCaptor.capture(), titleArgumentCaptor.capture(), thumbnailUrlArgumentCaptor.capture());

        // Create할 때 인수로 넘겨진 Youtube Id를 모아놨을 때 기대되는 Create Youtube ID 목록과 구성이 일치하는지 검사.
        List<Channel> argChannels = IntStream.range(0, tc.expectedCreatedChannels.size())
                .mapToObj(i -> {
                    String argYoutubeId = youtubeIdArgumentCaptor.getAllValues().get(i);
                    String argTitle = titleArgumentCaptor.getAllValues().get(i);
                    String argThumbnailUrl = thumbnailUrlArgumentCaptor.getAllValues().get(i);

                    return new Channel(argYoutubeId, argTitle, argThumbnailUrl);
                }).toList();
        assertThat(argChannels).containsExactlyInAnyOrderElementsOf(tc.expectedCreatedChannels);

        assertThat(collectedCount).isEqualTo(tc.expectedCreatedChannels.size());
    }

    private static class ChannelMockYoutubeApiCaller implements YoutubeDataApiCaller {
        @Override
        public ChannelListResponse fetchChannels(List<String> channelYoutubeIds) {
            return channelListResponse(channelYoutubeIds.stream()
                    .map(youtubeId -> channelResponse(makeChannel(youtubeId)))
                    .toList());
        }

        @Override
        public VideoListResponse fetchVideos(List<String> videoYoutubeId) {
            return null;
        }

        @Override
        public TrendingListResponse fetchTrendings(int maxResultCount, String regionCode, String pageToken) {
            return null;
        }

        @Override
        public RegionListResponse fetchRegions() {
            return null;
        }
    }

    private static Channel makeChannel(String youtubeId) {
        return new Channel(youtubeId, defaultTitle(youtubeId), defaultThumbnailUrl(youtubeId));
    }

    private static ChannelResponse channelResponse(Channel channel) {
        ChannelResponse.Snippet.Thumbnails.Thumbnail high = new ChannelResponse.Snippet.Thumbnails.Thumbnail(channel.thumbnailUrl);

        ChannelResponse.Snippet.Thumbnails thumbs = new ChannelResponse.Snippet.Thumbnails(high);

        ChannelResponse.Snippet snippet = new ChannelResponse.Snippet(channel.title, thumbs);

        ChannelResponse resp = new ChannelResponse(channel.youtubeId, snippet);

        return resp;
    }

    private static ChannelListResponse channelListResponse(List<ChannelResponse> items) {
        return new ChannelListResponse(items);
    }

    private static String defaultTitle(String youtubeId) {
        return String.format("title-%s", youtubeId);
    }

    private static String defaultThumbnailUrl(String youtubeId) {
        return String.format("thumbnailUrl-%s", youtubeId);
    }
}