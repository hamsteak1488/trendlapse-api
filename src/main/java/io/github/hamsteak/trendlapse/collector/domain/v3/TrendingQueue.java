package io.github.hamsteak.trendlapse.collector.domain.v3;


import java.util.ArrayDeque;
import java.util.Queue;

public class TrendingQueue {
    private final Queue<RegionTrendingItem> queue = new ArrayDeque<>();

    public void add(long regionId, int rank, String videoYoutubeId) {
        queue.add(new RegionTrendingItem(regionId, rank, videoYoutubeId));
    }

    public void add(RegionTrendingItem regionTrendingItem) {
        queue.add(regionTrendingItem);
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
}
