package io.github.hamsteak.trendlapse.collector.application.component.collector.v0;

import io.github.hamsteak.trendlapse.collector.application.component.collector.ChannelCollector;
import io.github.hamsteak.trendlapse.collector.application.component.collector.VideoCollector;
import io.github.hamsteak.trendlapse.collector.application.component.fetcher.VideoFetcher;
import io.github.hamsteak.trendlapse.collector.application.component.storer.VideoStorer;
import io.github.hamsteak.trendlapse.collector.application.dto.VideoItem;
import io.github.hamsteak.trendlapse.video.application.component.VideoFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@ConditionalOnProperty(prefix = "collector", name = "video-strategy", havingValue = "one-by-one")
@Slf4j
@Component
@RequiredArgsConstructor
public class OneByOneVideoCollector implements VideoCollector {
    private final ChannelCollector channelCollector;
    private final VideoFinder videoFinder;
    private final VideoFetcher videoFetcher;
    private final VideoStorer videoStorer;

    @Override
    public int collect(List<String> videoYoutubeIds) {
        videoYoutubeIds = videoFinder.findMissingVideoYoutubeIds(videoYoutubeIds.stream().distinct().toList());

        List<VideoItem> videoItems = videoFetcher.fetch(videoYoutubeIds, 1);

        List<String> channelYoutubeIds = videoItems.stream().map(VideoItem::getChannelYoutubeId).toList();
        channelCollector.collect(channelYoutubeIds);

        return videoStorer.store(videoItems);
    }
}
