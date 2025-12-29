package io.github.hamsteak.trendlapse.channel.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    List<Channel> findByYoutubeIdIn(List<String> youtubeIds);
}
