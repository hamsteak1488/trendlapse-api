package io.github.hamsteak.trendlapse.collector.domain;

import io.github.hamsteak.trendlapse.collector.application.component.collector.TrendingCollector;
import io.github.hamsteak.trendlapse.collector.application.component.collector.VideoCollector;
import io.github.hamsteak.trendlapse.collector.application.component.collector.v0.OneByOneTrendingCollector;
import io.github.hamsteak.trendlapse.collector.application.component.collector.v1.BatchTrendingCollector;
import io.github.hamsteak.trendlapse.collector.application.component.collector.v2.BufferedBatchTrendingCollector;
import io.github.hamsteak.trendlapse.collector.application.component.collector.v3.FlexibleBufferedBatchTrendingCollector;
import io.github.hamsteak.trendlapse.collector.application.component.collector.v3.FlexibleTrendingBuffer;
import io.github.hamsteak.trendlapse.collector.application.component.fetcher.TrendingFetcher;
import io.github.hamsteak.trendlapse.collector.application.component.storer.TrendingStorer;
import io.github.hamsteak.trendlapse.collector.application.dto.TrendingItem;
import io.github.hamsteak.trendlapse.video.application.component.VideoFinder;
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

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrendingCollectorTest {
    interface TrendingCollectorFactory {
        TrendingCollector create(Fixture fix);
    }

    @Builder
    static class Fixture {
        final TrendingFetcher trendingFetcher;
        final TrendingStorer trendingStorer;
        final VideoCollector videoCollector;
        final YoutubeDataApiProperties youtubeDataApiProperties;
        final FlexibleTrendingBuffer flexibleTrendingBuffer;
    }

    static Stream<Named<TrendingCollectorFactory>> implementations() {
        return Stream.of(
                Named.of("OneByOneTrendingCollector",
                        fix -> new OneByOneTrendingCollector(
                                fix.trendingFetcher,
                                fix.trendingStorer,
                                fix.videoCollector
                        )
                ),
                Named.of("BatchTrendingCollector",
                        fix -> new BatchTrendingCollector(
                                fix.trendingFetcher,
                                fix.trendingStorer,
                                fix.videoCollector,
                                fix.youtubeDataApiProperties
                        )
                ),
                Named.of("BufferedBatchTrendingCollector",
                        fix -> new BufferedBatchTrendingCollector(
                                fix.trendingFetcher,
                                fix.trendingStorer,
                                fix.videoCollector,
                                fix.youtubeDataApiProperties
                        )
                ),
                Named.of("FlexibleBufferedBatchTrendingCollector",
                        fix -> new FlexibleBufferedBatchTrendingCollector(
                                fix.trendingFetcher,
                                fix.trendingStorer,
                                fix.videoCollector,
                                fix.youtubeDataApiProperties,
                                fix.flexibleTrendingBuffer

                        )
                )
        );
    }

    @Builder
    @RequiredArgsConstructor
    private static class Case {
        final String name;
        final int collectSize;
        final int maxResultCount;
        final List<TrendingItem> trendingItemsToBeFetched;
    }

    private static Stream<Case> cases() {
        return Stream.of(
                Case.builder()
                        .name("한 개의 데이터")
                        .collectSize(1)
                        .maxResultCount(1)
                        .trendingItemsToBeFetched(List.of(defaultTrendingItem(1, "RG1")))
                        .build(),
                Case.builder()
                        .name("여러 데이터")
                        .collectSize(3)
                        .maxResultCount(3)
                        .trendingItemsToBeFetched(List.of(
                                defaultTrendingItem(1, "RG1"),
                                defaultTrendingItem(2, "RG1"),
                                defaultTrendingItem(3, "RG1")
                        ))
                        .build(),
                Case.builder()
                        .name("여러 지역 & 여러 데이터")
                        .collectSize(3)
                        .maxResultCount(3)
                        .trendingItemsToBeFetched(List.of(
                                defaultTrendingItem(1, "RG1"),
                                defaultTrendingItem(2, "RG1"),
                                defaultTrendingItem(3, "RG1"),
                                defaultTrendingItem(1, "RG2"),
                                defaultTrendingItem(2, "RG2"),
                                defaultTrendingItem(3, "RG2")
                        ))
                        .build(),
                Case.builder()
                        .name("여러 Fetch")
                        .collectSize(3)
                        .maxResultCount(1)
                        .trendingItemsToBeFetched(List.of(
                                defaultTrendingItem(1, "RG1"),
                                defaultTrendingItem(2, "RG1"),
                                defaultTrendingItem(3, "RG1"),
                                defaultTrendingItem(1, "RG2"),
                                defaultTrendingItem(2, "RG2"),
                                defaultTrendingItem(3, "RG2")
                        ))
                        .build(),
                Case.builder()
                        .name("collectSize가 maxResult로 딱 나누어 떨어지지 않을 때")
                        .collectSize(5)
                        .maxResultCount(2)
                        .trendingItemsToBeFetched(List.of(
                                defaultTrendingItem(1, "RG1"),
                                defaultTrendingItem(2, "RG1"),
                                defaultTrendingItem(3, "RG1"),
                                defaultTrendingItem(4, "RG1"),
                                defaultTrendingItem(5, "RG1"),
                                defaultTrendingItem(1, "RG2"),
                                defaultTrendingItem(2, "RG2"),
                                defaultTrendingItem(3, "RG2"),
                                defaultTrendingItem(4, "RG2"),
                                defaultTrendingItem(5, "RG2")
                        ))
                        .build(),
                Case.builder()
                        .name("collectSize가 maxResult보다 작을 때")
                        .collectSize(3)
                        .maxResultCount(10)
                        .trendingItemsToBeFetched(List.of(
                                defaultTrendingItem(1, "RG1"),
                                defaultTrendingItem(2, "RG1"),
                                defaultTrendingItem(3, "RG1")
                        ))
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
    @DisplayName("모든 TrendingCollector 구현체에 대해 검증")
    void create_expected_items_from_api_items(String implName, TrendingCollectorFactory trendingCollectorFactory, String tcName, Case tc) {
        // given
        YoutubeDataApiProperties youtubeDataApiProperties =
                new YoutubeDataApiProperties("baseUrl", "apiKey", tc.maxResultCount, false, 3, 10);

        TrendingFetcher trendingFetcher = new MockTrendingFetcher(tc.trendingItemsToBeFetched);
        TrendingStorer trendingStorer = mock(TrendingStorer.class);
        VideoCollector videoCollector = mock(VideoCollector.class);

        VideoFinder videoFinder = mock(VideoFinder.class);
        FlexibleTrendingBuffer flexibleTrendingBuffer = new FlexibleTrendingBuffer(videoFinder);

        List<String> regionCodes = tc.trendingItemsToBeFetched.stream().map(TrendingItem::getRegionCode).distinct().toList();

        lenient().when(videoFinder.existsByYoutubeId(anyString())).thenReturn(false);

        Fixture fix = Fixture.builder()
                .trendingFetcher(trendingFetcher)
                .trendingStorer(trendingStorer)
                .videoCollector(videoCollector)
                .youtubeDataApiProperties(youtubeDataApiProperties)
                .flexibleTrendingBuffer(flexibleTrendingBuffer)
                .build();

        TrendingCollector trendingCollector = trendingCollectorFactory.create(fix);

        // when
        trendingCollector.collect(defaultLocalDateTime(), tc.collectSize, regionCodes);

        // then
        ArgumentCaptor<List> storeTrendingItemsArgCaptor = ArgumentCaptor.forClass(List.class);

        verify(trendingStorer, atLeast(0)).store(storeTrendingItemsArgCaptor.capture());

        List<TrendingItem> argTrendingItems = storeTrendingItemsArgCaptor.getAllValues().stream()
                .flatMap(list -> ((List<TrendingItem>) list).stream()).toList();

        assertThat(argTrendingItems).containsExactlyInAnyOrderElementsOf(tc.trendingItemsToBeFetched);
    }

    private static TrendingItem defaultTrendingItem(int rank, String regionCode) {
        return new TrendingItem(defaultLocalDateTime(), regionCode, rank, defaultVideoYoutubeId(regionCode, rank));
    }

    private static String defaultVideoYoutubeId(String regionCode, int rank) {
        return String.format("%s-videoYoutubeId-%d", regionCode, rank);
    }

    private static LocalDateTime defaultLocalDateTime() {
        return LocalDateTime.of(2025, 1, 1, 0, 0);
    }

    private static class MockTrendingFetcher implements TrendingFetcher {
        private final Map<String, List<TrendingItem>> trendingItemsMap;

        public MockTrendingFetcher(List<TrendingItem> trendingItemsToBeFetched) {
            trendingItemsMap = trendingItemsToBeFetched.stream()
                    .collect(Collectors.groupingBy(TrendingItem::getRegionCode));
        }

        @Override
        public List<TrendingItem> fetch(LocalDateTime dateTime, int collectSize, List<String> regionCodes, int maxResultCount) {
            return regionCodes.stream()
                    .map(trendingItemsMap::get)
                    .flatMap(Collection::stream)
                    .toList();
        }
    }
}