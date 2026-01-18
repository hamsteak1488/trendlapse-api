package io.github.hamsteak.trendlapse.video.application;

import io.github.hamsteak.trendlapse.video.application.dto.VideoSearchFilter;
import io.github.hamsteak.trendlapse.video.application.dto.VideoView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

public interface VideoQueryRepository {
    PagedModel<VideoView> search(VideoSearchFilter filter, Pageable pageable);
}
