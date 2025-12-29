package io.github.hamsteak.trendlapse.channel.domain;

import java.util.List;

public interface ChannelBulkInsertRepository {
    void bulkInsert(List<Channel> channels);
}
