package io.github.hamsteak.trendlapse.collector.domain.v3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Queue;

@Component
public class VideoUncollectedTrendingQueue {
    private final Queue<RegionTrendingItem> queue = new ArrayDeque<>();

    public void add(long regionId, int rank, String videoYoutubeId) {
        queue.add(new RegionTrendingItem(regionId, rank, videoYoutubeId));
    }

    public RegionTrendingItem poll() {
        return queue.poll();
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Getter
    @RequiredArgsConstructor
    public static class RegionTrendingItem {
        private final long regionId;
        private final int rank;
        private final String videoYoutubeId;
    }
}
