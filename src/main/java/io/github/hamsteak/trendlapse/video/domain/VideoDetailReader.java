package io.github.hamsteak.trendlapse.video.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class VideoDetailReader {
    private final VideoReader videoReader;

    @Transactional(readOnly = true)
    public VideoDetail read(long videoId) {
        Video video = videoReader.read(videoId);

        return VideoDetail.builder()
                .id(videoId)
                .channelId(video.getChannel().getId())
                .youtubeId(video.getYoutubeId())
                .title(video.getTitle())
                .thumbnailUrl(video.getThumbnailUrl())
                .build();
    }
}
