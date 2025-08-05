package io.github.hamsteak.trendlapse.video.domain;

import io.github.hamsteak.trendlapse.common.errors.exception.VideoNotFoundException;
import io.github.hamsteak.trendlapse.video.infrastructure.VideoRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class VideoReader {
    private final VideoRepository videoRepository;

    @Transactional(readOnly = true)
    public @NotNull Video read(long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new VideoNotFoundException("Cannot find video (id:" + videoId + ")"));
    }

    @Transactional(readOnly = true)
    public @NotNull Video readByYoutubeId(String videoYoutubeId) {
        return videoRepository.findByYoutubeId(videoYoutubeId)
                .orElseThrow(() -> new VideoNotFoundException("Cannot find video (videoYoutubeId:" + videoYoutubeId + ")"));
    }
}
