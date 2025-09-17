package io.github.hamsteak.trendlapse.collector.domain.v3;


import io.github.hamsteak.trendlapse.collector.domain.TrendingItem;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayDeque;
import java.util.Queue;

public class TrendingQueue {
    private final Queue<TrendingItem> queue = new ArrayDeque<>();

    public void add(@NotNull TrendingItem trendingItem) {
        queue.add(trendingItem);
    }

    public @NotNull TrendingItem poll() {
        if (queue.isEmpty()) {
            throw new IllegalStateException("TrendingQueue is empty.");
        }
        return queue.poll();
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
