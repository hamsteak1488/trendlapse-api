package io.github.hamsteak.youtubetimelapse.channel.domain;

import io.github.hamsteak.youtubetimelapse.channel.infrastructure.ChannelRepository;
import io.github.hamsteak.youtubetimelapse.common.errors.errorcode.CommonErrorCode;
import io.github.hamsteak.youtubetimelapse.common.errors.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChannelReader {
    private final ChannelRepository channelRepository;

    @Transactional(readOnly = true)
    public Channel read(long channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND, "Cannot find channel (id:" + channelId + ")"));
    }

    @Transactional(readOnly = true)
    public Channel readByYoutubeId(String youtubeId) {
        return channelRepository.findByYoutubeId(youtubeId)
                .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND, "Cannot find channel (youtubeId:" + youtubeId + ")"));
    }

    @Transactional(readOnly = true)
    public List<Channel> readByYoutubeIds(List<String> youtubeIds) {
        return channelRepository.findByYoutubeIdIn(youtubeIds);
    }
}
