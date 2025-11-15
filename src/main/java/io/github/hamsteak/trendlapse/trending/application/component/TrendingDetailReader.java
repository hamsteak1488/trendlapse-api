package io.github.hamsteak.trendlapse.trending.application.component;

import io.github.hamsteak.trendlapse.channel.application.component.ChannelDetailReader;
import io.github.hamsteak.trendlapse.channel.application.dto.ChannelDetail;
import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.trending.domain.Trending;
import io.github.hamsteak.trendlapse.trending.application.dto.TrendingDetail;
import io.github.hamsteak.trendlapse.video.domain.Video;
import io.github.hamsteak.trendlapse.video.application.dto.VideoDetail;
import io.github.hamsteak.trendlapse.video.application.component.VideoDetailReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TrendingDetailReader {
    private final TrendingReader trendingReader;
    private final VideoDetailReader videoDetailReader;
    private final ChannelDetailReader channelDetailReader;

    @Transactional(readOnly = true)
    public TrendingDetail read(long trendingId) {
        Trending trending = trendingReader.read(trendingId);

        Video video = trending.getVideo();
        Channel channel = video.getChannel();

        VideoDetail videoDetail = videoDetailReader.read(video.getId());
        ChannelDetail channelDetail = channelDetailReader.read(channel.getId());

        return TrendingDetail.builder()
                .dateTime(trending.getDateTime())
                .rank(trending.getRankValue())
                .videoDetail(videoDetail)
                .channelDetail(channelDetail)
                .build();
    }
}
