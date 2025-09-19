package io.github.hamsteak.trendlapse.collector.domain.v1;

import io.github.hamsteak.trendlapse.collector.domain.ChannelCollector;
import io.github.hamsteak.trendlapse.collector.domain.VideoCollector;
import io.github.hamsteak.trendlapse.collector.domain.VideoItem;
import io.github.hamsteak.trendlapse.collector.fetcher.VideoFetcher;
import io.github.hamsteak.trendlapse.collector.storer.VideoStorer;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import io.github.hamsteak.trendlapse.video.domain.VideoFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@ConditionalOnProperty(prefix = "collector", name = "video-strategy", havingValue = "batch", matchIfMissing = true)
@Slf4j
@Component
@RequiredArgsConstructor
public class BatchVideoCollector implements VideoCollector {
    private final ChannelCollector channelCollector;
    private final VideoFinder videoFinder;
    private final VideoFetcher videoFetcher;
    private final VideoStorer videoStorer;
    private final YoutubeDataApiProperties youtubeDataApiProperties;

    public int collect(List<String> videoYoutubeIds) {
        videoYoutubeIds = videoFinder.findMissingVideoYoutubeIds(videoYoutubeIds.stream().distinct().toList());

        log.info("Found {} missing videos.", videoYoutubeIds.size());

        List<VideoItem> videoItems = videoFetcher.fetch(videoYoutubeIds, youtubeDataApiProperties.getMaxResultCount());

        List<String> channelYoutubeIds = videoItems.stream().map(VideoItem::getChannelYoutubeId).distinct().toList();
        channelCollector.collect(channelYoutubeIds);

        int storedCount = videoStorer.store(videoItems);

        log.info("Stored {} videos.", storedCount);

        return storedCount;
    }
}
