package io.github.hamsteak.trendlapse.video.domain;

import io.github.hamsteak.trendlapse.common.errors.exception.VideoNotFoundException;
import io.github.hamsteak.trendlapse.video.infrastructure.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VideoReader {
    private final VideoRepository videoRepository;

    @Transactional(readOnly = true)
    public Video read(long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new VideoNotFoundException("Cannot find video (id:" + videoId + ")"));
    }

    @Transactional(readOnly = true)
    public Video readByYoutubeId(String youtubeId) {
        return videoRepository.findByYoutubeId(youtubeId)
                .orElseThrow(() -> new VideoNotFoundException("Cannot find video (youtubeId:" + youtubeId + ")"));
    }

    @Transactional(readOnly = true)
    public List<Video> readByYoutubeIds(List<String> youtubeIds) {
        return videoRepository.findByYoutubeIdIn(youtubeIds);
    }
}
