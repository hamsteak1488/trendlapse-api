package io.github.hamsteak.youtubetimelapse.external.youtube;

import io.github.hamsteak.youtubetimelapse.external.youtube.dto.ChannelResponse;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoListResponse;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoResponse;

public interface YoutubeDataApiCaller {
    ChannelResponse getChannel(String channelYoutubeId);
    VideoResponse getVideo(String videoYoutubeId);
    VideoListResponse getTrendings();
}
