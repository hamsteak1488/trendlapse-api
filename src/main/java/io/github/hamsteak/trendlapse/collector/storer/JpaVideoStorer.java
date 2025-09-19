package io.github.hamsteak.trendlapse.collector.storer;

import io.github.hamsteak.trendlapse.collector.domain.VideoItem;
import io.github.hamsteak.trendlapse.common.errors.exception.ChannelNotFoundException;
import io.github.hamsteak.trendlapse.video.domain.VideoCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JpaVideoStorer implements VideoStorer {
    private final VideoCreator videoCreator;

    public int store(List<VideoItem> videoItems) {
        int storedCount = 0;

        for (VideoItem videoItem : videoItems) {
            String videoYoutubeId = videoItem.getYoutubeId();
            String channelYoutubeId = videoItem.getChannelYoutubeId();

            try {
                videoCreator.create(
                        videoYoutubeId,
                        channelYoutubeId,
                        videoItem.getTitle(),
                        videoItem.getThumbnailUrl()
                );
                storedCount++;
            } catch (ChannelNotFoundException ex) {
                log.info("Skipping video record creation: No matching channel found (videoYoutubeId={}, channelYoutubeId={}).",
                        videoYoutubeId, channelYoutubeId);
            }
        }

        return storedCount;
    }
}
