package io.github.hamsteak.youtubetimelapse.external.youtube;

import io.github.hamsteak.youtubetimelapse.external.youtube.dto.ChannelResponse;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoListResponse;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoResponse;

import java.util.List;

public interface YoutubeDataApiCaller {
    ChannelResponse getChannel(String channelYoutubeId);
    VideoResponse getVideo(String videoYoutubeId);
    List<VideoListResponse> getTrendings(int count);
}
