package io.github.hamsteak.youtubetimelapse.external.youtube;

import io.github.hamsteak.youtubetimelapse.external.youtube.dto.*;

import java.util.List;

public interface YoutubeDataApiCaller {
    ChannelResponse fetchChannel(String channelYoutubeId);
    List<ChannelListResponse> fetchChannels(List<String> channelYoutubeId);
    VideoResponse fetchVideo(String videoYoutubeId);
    List<VideoListResponse> fetchVideos(List<String> videoYoutubeId);
    List<VideoListResponse> fetchTrendings(int count, String regionCode);
}
