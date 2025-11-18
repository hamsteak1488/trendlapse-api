package io.github.hamsteak.trendlapse.video.application.component;

import io.github.hamsteak.trendlapse.video.application.dto.VideoCreateDto;

import java.util.List;

public interface VideoCreator {
    int create(List<VideoCreateDto> videoCreateDtos);
}
