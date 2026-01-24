package io.github.hamsteak.trendlapse.video.web;

import io.github.hamsteak.trendlapse.video.application.SearchVideoService;
import io.github.hamsteak.trendlapse.video.application.dto.SearchVideoCommand;
import io.github.hamsteak.trendlapse.video.application.dto.VideoView;
import io.github.hamsteak.trendlapse.video.web.dto.SearchVideoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController {
    private final SearchVideoService searchVideoService;

    @GetMapping
    public ResponseEntity<PagedModel<VideoView>> search(SearchVideoRequest request, Pageable pageable) {
        PagedModel<VideoView> videoViewPage = searchVideoService.search(
                new SearchVideoCommand(request.getChannelId(), request.getYoutubeId(), request.getTitle()),
                pageable);

        return ResponseEntity.ok(videoViewPage);
    }
}
