package io.github.hamsteak.trendlapse.collector.fetcher;

import io.github.hamsteak.trendlapse.collector.domain.VideoItem;
import io.github.hamsteak.trendlapse.external.youtube.dto.VideoListResponse;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchVideoFetcher implements VideoFetcher {
    private final YoutubeDataApiCaller youtubeDataApiCaller;

    public List<VideoItem> fetch(List<String> videoYoutubeIds, int maxResultCount) {
        List<VideoItem> items = new ArrayList<>();

        int startIndex = 0;
        while (startIndex < videoYoutubeIds.size()) {
            int endIndex = Math.min(startIndex + maxResultCount, videoYoutubeIds.size());
            List<String> subFetchVideoYoutubeIds = videoYoutubeIds.subList(startIndex, endIndex);

            VideoListResponse videoListResponse = youtubeDataApiCaller.fetchVideos(subFetchVideoYoutubeIds);
            items.addAll(videoListResponse.getItems().stream()
                    .map(videoResponse -> new VideoItem(
                            videoResponse.getId(),
                            videoResponse.getChannelYoutubeId(),
                            videoResponse.getSnippet().getTitle(),
                            videoResponse.getSnippet().getThumbnails().getHigh().getUrl()
                    )).toList());

            startIndex += maxResultCount;
        }

        if (items.size() != videoYoutubeIds.size()) {
            List<String> videoYoutubeIdsFromItems = items.stream().map(VideoItem::getYoutubeId).toList();
            List<String> diff = videoYoutubeIds.stream().filter(videoYoutubeId -> !videoYoutubeIdsFromItems.contains(videoYoutubeId)).toList();
            log.info("Expected {} videos, but only {} returned. Difference: {}", videoYoutubeIds.size(), videoYoutubeIdsFromItems.size(), diff);
        }

        return items;
    }
}
