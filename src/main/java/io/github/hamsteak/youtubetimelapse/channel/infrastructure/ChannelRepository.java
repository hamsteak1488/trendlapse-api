package io.github.hamsteak.youtubetimelapse.channel.infrastructure;

import io.github.hamsteak.youtubetimelapse.channel.domain.Channel;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ChannelRepository extends Repository<Channel, Long> {
    Channel save(Channel channel);
    Optional<Channel> findByYoutubeId(String youtubeId);
}
