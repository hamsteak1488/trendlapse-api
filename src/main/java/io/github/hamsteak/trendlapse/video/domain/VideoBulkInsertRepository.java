package io.github.hamsteak.trendlapse.video.domain;

import java.util.List;

public interface VideoBulkInsertRepository {
    void bulkInsert(List<Video> videos);
}
