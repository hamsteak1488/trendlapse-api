package io.github.hamsteak.trendlapse.external.youtube.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class VideoListResponse {
    private final List<VideoResponse> items;
}
