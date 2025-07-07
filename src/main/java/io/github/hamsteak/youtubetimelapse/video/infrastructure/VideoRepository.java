package io.github.hamsteak.youtubetimelapse.video.infrastructure;

import io.github.hamsteak.youtubetimelapse.video.domain.Video;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface VideoRepository extends Repository<Video, Long> {
    Video save(Video video);
    Optional<Video> findByYoutubeId(String youtubeId);
}
