package io.github.hamsteak.youtubetimelapse.trending.domain;

import io.github.hamsteak.youtubetimelapse.channel.domain.Channel;
import io.github.hamsteak.youtubetimelapse.channel.domain.ChannelDetail;
import io.github.hamsteak.youtubetimelapse.trending.domain.dto.TrendingSearchFilter;
import io.github.hamsteak.youtubetimelapse.trending.infrastructure.TrendingRepository;
import io.github.hamsteak.youtubetimelapse.video.domain.Video;
import io.github.hamsteak.youtubetimelapse.video.domain.VideoDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrendingSearcher {
    private final TrendingRepository trendingRepository;

    @Transactional(readOnly = true)
    public List<DateTimeTrendingDetailList> search(TrendingSearchFilter filter) {
        return trendingRepository.findByDateTimeBetween(filter.getStartDateTime(), filter.getEndDateTime()).stream()
                .map(mapFromTrendingToTrendingDetail())
                .collect(Collectors.groupingBy(TrendingDetail::getDateTime, LinkedHashMap::new, Collectors.toList()))
                .entrySet().stream()
                .map(mapFromTrendingDetailMapToDateTimeTrendingDetailList())
                .toList();
    }

    private static Function<Trending, TrendingDetail> mapFromTrendingToTrendingDetail() {
        return trending -> {
            Video video = trending.getVideo();
            Channel channel = video.getChannel();

            VideoDetail videoDetail = VideoDetail.builder()
                    .id(video.getId())
                    .youtubeId(video.getYoutubeId())
                    .title(video.getTitle())
                    .channelId(channel.getId())
                    .build();
            ChannelDetail channelDetail = ChannelDetail.builder()
                    .id(channel.getId())
                    .youtubeId(channel.getYoutubeId())
                    .title(channel.getTitle())
                    .build();

            return TrendingDetail.builder()
                    .dateTime(trending.getDateTime())
                    .rank(trending.getRank())
                    .videoDetail(videoDetail)
                    .channelDetail(channelDetail)
                    .build();
        };

    }

    private static Function<Map.Entry<LocalDateTime, List<TrendingDetail>>, DateTimeTrendingDetailList> mapFromTrendingDetailMapToDateTimeTrendingDetailList() {
        return entry -> {
            LocalDateTime dateTime = entry.getKey();
            List<TrendingDetail> trendingDetails = entry.getValue();

            return DateTimeTrendingDetailList.builder()
                    .dateTime(dateTime)
                    .items(trendingDetails)
                    .build();
        };
    }
}
