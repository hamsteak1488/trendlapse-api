package io.github.hamsteak.trendlapse.collector.application.component.storer;

import io.github.hamsteak.trendlapse.channel.application.component.ChannelFinder;
import io.github.hamsteak.trendlapse.collector.application.dto.VideoItem;
import io.github.hamsteak.trendlapse.video.application.component.VideoCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JpaVideoStorer implements VideoStorer {
    private final VideoCreator videoCreator;
    private final ChannelFinder channelFinder;

    @Override
    public int store(List<VideoItem> videoItems) {
        videoItems = getChannelMissingExcludedVideoItems(videoItems);

        int storedCount = 0;

        for (VideoItem videoItem : videoItems) {
            String videoYoutubeId = videoItem.getYoutubeId();
            String channelYoutubeId = videoItem.getChannelYoutubeId();

            videoCreator.create(
                    videoYoutubeId,
                    channelYoutubeId,
                    videoItem.getTitle(),
                    videoItem.getThumbnailUrl()
            );
            storedCount++;
        }

        return storedCount;
    }

    private List<VideoItem> getChannelMissingExcludedVideoItems(List<VideoItem> videoItems) {
        List<String> channelYoutubeIds = videoItems.stream().map(VideoItem::getChannelYoutubeId).distinct().toList();
        List<String> missingChannelYoutubeIds = channelFinder.findMissingChannelYoutubeIds(channelYoutubeIds);

        List<VideoItem> channelMissingVideoItems = videoItems.stream()
                .filter(videoItem -> missingChannelYoutubeIds.contains(videoItem.getChannelYoutubeId()))
                .toList();
        channelMissingVideoItems.forEach(videoItem ->
                log.info("Skipping video record creation: No matching channel found (videoYoutubeId={}, channelYoutubeId={}).",
                        videoItem.getYoutubeId(), videoItem.getChannelYoutubeId())
        );

        return videoItems.stream()
                .filter(videoItem -> !missingChannelYoutubeIds.contains(videoItem.getChannelYoutubeId()))
                .toList();
    }
}
