package io.github.hamsteak.youtubetimelapse.trending.domain;

import io.github.hamsteak.youtubetimelapse.trending.domain.dto.TrendingSearchFilter;
import io.github.hamsteak.youtubetimelapse.trending.infrastructure.TrendingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TrendingSearcher {
    private final TrendingRepository trendingRepository;

    @Transactional(readOnly = true)
    public List<DateTimeTrendingDetailList> search(TrendingSearchFilter filter) {
        List<DateTimeTrendingDetailList> result = new ArrayList<>();
        
        for (LocalDateTime dateTime = filter.getStartDateTime();
             !dateTime.isAfter(filter.getEndDateTime());
             dateTime = dateTime.plusMinutes(15)
        ) {
            List<TrendingDetail> trendingDetails = trendingRepository.findByDateTime(dateTime).stream()
                    .map(trending -> TrendingDetail.builder()
                            .dateTime(trending.getDateTime())
                            .youtubeId(trending.getVideo().getYoutubeId())
                            .rank(trending.getRank())
                            .build())
                    .toList();

            result.add(DateTimeTrendingDetailList.builder()
                    .dateTime(dateTime)
                    .items(trendingDetails)
                    .build());
        }

        return result;
    }
}
