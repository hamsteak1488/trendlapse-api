package io.github.hamsteak.trendlapse.channel.infrastructure;

import io.github.hamsteak.trendlapse.channel.domain.Channel;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface ChannelRepository extends Repository<Channel, Long> {
    Channel save(Channel channel);

    Optional<Channel> findById(long id);

    Optional<Channel> findByYoutubeId(String youtubeId);

    boolean existsByYoutubeId(String youtubeId);

    List<Channel> findByYoutubeIdIn(List<String> youtubeIds);
}
