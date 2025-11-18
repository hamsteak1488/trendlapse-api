package io.github.hamsteak.trendlapse.collector.domain;

import io.github.hamsteak.trendlapse.channel.application.component.ChannelFinder;
import io.github.hamsteak.trendlapse.collector.application.component.collector.ChannelCollector;
import io.github.hamsteak.trendlapse.collector.application.component.collector.v0.OneByOneChannelCollector;
import io.github.hamsteak.trendlapse.collector.application.component.collector.v1.BatchChannelCollector;
import io.github.hamsteak.trendlapse.collector.application.component.fetcher.ChannelFetcher;
import io.github.hamsteak.trendlapse.collector.application.component.storer.ChannelStorer;
import io.github.hamsteak.trendlapse.collector.application.dto.ChannelItem;
import io.github.hamsteak.trendlapse.youtube.infrastructure.YoutubeDataApiProperties;
import lombok.Builder;
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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChannelCollectorTest {
    private interface ChannelCollectorFactory {
        ChannelCollector create(Fixture fix);
    }

    @Builder
    private static class Fixture {
        final ChannelFinder channelFinder;
        final ChannelFetcher channelFetcher;
        final ChannelStorer channelStorer;
        final YoutubeDataApiProperties youtubeDataApiProperties;
    }

    private static Stream<Named<ChannelCollectorFactory>> implementations() {
        return Stream.of(
                Named.of("OneByOneChannelCollector",
                        (fix) -> new OneByOneChannelCollector(
                                fix.channelFinder,
                                fix.channelFetcher,
                                fix.channelStorer
                        )
                ),
                Named.of("BatchChannelCollector",
                        (fix) -> new BatchChannelCollector(
                                fix.channelFinder,
                                fix.channelFetcher,
                                fix.channelStorer,
                                fix.youtubeDataApiProperties
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
        final List<ChannelItem> expectedStoredChannels;
    }

    private static Stream<Case> cases() {
        return Stream.of(
                Case.builder()
                        .name("한 개의 데이터")
                        .maxResultCount(50)
                        .requestChannelYoutubeIds(List.of("A"))
                        .existingChannelYoutubeIds(List.of())
                        .expectedStoredChannels(List.of(defaultChannelItem("A")))
                        .build(),
                Case.builder()
                        .name("여러 개의 데이터")
                        .maxResultCount(50)
                        .requestChannelYoutubeIds(List.of("A", "B", "C"))
                        .existingChannelYoutubeIds(List.of())
                        .expectedStoredChannels(List.of(defaultChannelItem("A"), defaultChannelItem("B"), defaultChannelItem("C")))
                        .build(),
                Case.builder()
                        .name("DB에 없는 데이터만 DB에 저장하는지 검사")
                        .maxResultCount(50)
                        .requestChannelYoutubeIds(List.of("A", "B", "C"))
                        .existingChannelYoutubeIds(List.of("B"))
                        .expectedStoredChannels(List.of(defaultChannelItem("A"), defaultChannelItem("C")))
                        .build(),
                Case.builder()
                        .name("DB에 이미 모든 데이터가 존재")
                        .maxResultCount(50)
                        .requestChannelYoutubeIds(List.of("A", "B"))
                        .existingChannelYoutubeIds(List.of("A", "B"))
                        .expectedStoredChannels(List.of())
                        .build(),
                Case.builder()
                        .name("중복 제거 후 저장하는지 검사")
                        .maxResultCount(50)
                        .requestChannelYoutubeIds(List.of("A", "A", "B", "B"))
                        .existingChannelYoutubeIds(List.of())
                        .expectedStoredChannels(List.of(defaultChannelItem("A"), defaultChannelItem("B")))
                        .build(),
                Case.builder()
                        .name("MaxResultCount가 요청 데이터 목록 길이보다 작은 경우에도 정상 동작하는지 검사.")
                        .maxResultCount(2)
                        .requestChannelYoutubeIds(List.of("A", "B", "C", "D", "E"))
                        .existingChannelYoutubeIds(List.of())
                        .expectedStoredChannels(List.of(defaultChannelItem("A"), defaultChannelItem("B"), defaultChannelItem("C"), defaultChannelItem("D"), defaultChannelItem("E")))
                        .build(),
                Case.builder()
                        .name("MaxResultCount가 요청 데이터 목록 길이보다 작은 경우에도 정상 동작하는지 검사.")
                        .maxResultCount(2)
                        .requestChannelYoutubeIds(List.of("A", "B", "C", "D", "E"))
                        .existingChannelYoutubeIds(List.of())
                        .expectedStoredChannels(List.of(defaultChannelItem("A"), defaultChannelItem("B"), defaultChannelItem("C"), defaultChannelItem("D"), defaultChannelItem("E")))
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
        ChannelFetcher channelFetcher = new MockChannelFetcher();
        ChannelStorer channelStorer = mock(ChannelStorer.class);
        YoutubeDataApiProperties youtubeDataApiProperties = new YoutubeDataApiProperties("baseUrl", "apiKey", tc.maxResultCount, false, 3);

        when(channelFinder.findMissingChannelYoutubeIds(anyList()))
                .then(invocationOnMock -> {
                    List<String> channelYoutubeIds = invocationOnMock.getArgument(0);
                    return channelYoutubeIds.stream()
                            .filter(channelYoutubeId -> !tc.existingChannelYoutubeIds.contains(channelYoutubeId)).toList();
                });

        Fixture fix = Fixture.builder()
                .channelFetcher(channelFetcher)
                .channelStorer(channelStorer)
                .channelFinder(channelFinder)
                .youtubeDataApiProperties(youtubeDataApiProperties)
                .build();

        ChannelCollector channelCollector = channelCollectorFactory.create(fix);

        // when
        channelCollector.collect(tc.requestChannelYoutubeIds);

        // then
        ArgumentCaptor<List> storeChannelItemsArgCaptor = ArgumentCaptor.forClass(List.class);

        verify(channelStorer, atLeast(0)).store(storeChannelItemsArgCaptor.capture());

        List<ChannelItem> argChannelItems = storeChannelItemsArgCaptor.getAllValues().stream()
                .flatMap(list -> ((List<ChannelItem>) list).stream()).toList();

        assertThat(argChannelItems).containsExactlyInAnyOrderElementsOf(tc.expectedStoredChannels);
    }

    private static class MockChannelFetcher implements ChannelFetcher {
        @Override
        public List<ChannelItem> fetch(List<String> channelYoutubeIds, int maxResultCount) {
            return channelYoutubeIds.stream()
                    .map(ChannelCollectorTest::defaultChannelItem)
                    .toList();
        }
    }

    private static ChannelItem defaultChannelItem(String youtubeId) {
        return new ChannelItem(youtubeId, defaultTitle(youtubeId), defaultThumbnailUrl(youtubeId));
    }

    private static String defaultTitle(String youtubeId) {
        return String.format("title-%s", youtubeId);
    }

    private static String defaultThumbnailUrl(String youtubeId) {
        return String.format("thumbnailUrl-%s", youtubeId);
    }
}