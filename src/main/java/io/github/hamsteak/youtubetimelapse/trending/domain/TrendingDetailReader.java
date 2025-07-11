package io.github.hamsteak.youtubetimelapse.trending.domain;

import io.github.hamsteak.youtubetimelapse.channel.domain.Channel;
import io.github.hamsteak.youtubetimelapse.channel.domain.ChannelDetail;
import io.github.hamsteak.youtubetimelapse.channel.domain.ChannelDetailReader;
import io.github.hamsteak.youtubetimelapse.video.domain.Video;
import io.github.hamsteak.youtubetimelapse.video.domain.VideoDetail;
import io.github.hamsteak.youtubetimelapse.video.domain.VideoDetailReader;
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
                .rank(trending.getRank())
                .videoDetail(videoDetail)
                .channelDetail(channelDetail)
                .build();
    }
}
