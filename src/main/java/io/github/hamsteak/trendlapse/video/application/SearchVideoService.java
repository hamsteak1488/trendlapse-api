package io.github.hamsteak.trendlapse.video.application;

import io.github.hamsteak.trendlapse.video.application.dto.SearchVideoCommand;
import io.github.hamsteak.trendlapse.video.application.dto.VideoSearchFilter;
import io.github.hamsteak.trendlapse.video.application.dto.VideoView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchVideoService {
    private final VideoQueryRepository videoQueryRepository;

    @Transactional(readOnly = true)
    public PagedModel<VideoView> search(SearchVideoCommand command, Pageable pageable) {
        return videoQueryRepository.search(mapFromCommandToFilter(command), pageable);
    }

    private VideoSearchFilter mapFromCommandToFilter(SearchVideoCommand command) {
        return new VideoSearchFilter(
                command.getChannelId(),
                command.getYoutubeId(),
                command.getTitle()
        );
    }
}
