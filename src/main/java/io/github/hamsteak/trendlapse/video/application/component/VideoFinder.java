package io.github.hamsteak.trendlapse.video.application.component;

import io.github.hamsteak.trendlapse.video.domain.Video;
import io.github.hamsteak.trendlapse.video.infrastructure.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class VideoFinder {
    private final VideoRepository videoRepository;

    @Transactional(readOnly = true)
    public boolean existsByYoutubeId(String videoYoutubeId) {
        return videoRepository.existsByYoutubeId(videoYoutubeId);
    }

    @Transactional(readOnly = true)
    public List<Video> findExistingVideos(List<String> videoYoutubeIds) {
        return videoRepository.findByYoutubeIdIn(videoYoutubeIds);
    }

    @Transactional(readOnly = true)
    public List<String> findMissingVideoYoutubeIds(List<String> videoYoutubeIds) {
        Map<String, Video> existingVideoMap = new HashMap<>();
        findExistingVideos(videoYoutubeIds).forEach(
                video -> existingVideoMap.put(video.getYoutubeId(), video));

        return videoYoutubeIds.stream()
                .filter(videoYoutubeId -> !existingVideoMap.containsKey(videoYoutubeId))
                .toList();
    }
}
