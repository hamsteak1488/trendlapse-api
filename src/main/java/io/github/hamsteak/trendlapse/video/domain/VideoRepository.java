package io.github.hamsteak.trendlapse.video.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByYoutubeIdIn(List<String> youtubeIds);
}
