package io.github.hamsteak.trendlapse.video.infrastructure;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.hamsteak.trendlapse.video.application.VideoQueryRepository;
import io.github.hamsteak.trendlapse.video.application.dto.QVideoView;
import io.github.hamsteak.trendlapse.video.application.dto.VideoSearchFilter;
import io.github.hamsteak.trendlapse.video.application.dto.VideoView;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Repository;

import java.util.List;

import static io.github.hamsteak.trendlapse.video.domain.QVideo.video;

@Repository
public class VideoQueryRepositoryImpl implements VideoQueryRepository {
    private final JPAQueryFactory query;

    public VideoQueryRepositoryImpl(EntityManager entityManager) {
        query = new JPAQueryFactory(entityManager);
    }

    @Override
    public PagedModel<VideoView> search(VideoSearchFilter filter, Pageable pageable) {
        BooleanExpression condition = Expressions.FALSE
                .or(eqId(filter.getId()))
                .or(eqChannelId(filter.getChannelId()))
                .or(eqYoutubeId(filter.getYoutubeId()))
                .or(likeTitle(filter.getTitle()));

        List<VideoView> content = query
                .select(new QVideoView(video.id, video.channelId, video.youtubeId, video.title, video.thumbnailUrl))
                .from(video)
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = query
                .select(video.count())
                .from(video)
                .where(condition)
                .fetchFirst();

        return new PagedModel<>(new PageImpl<>(content, pageable, total));
    }

    private BooleanExpression eqId(Long id) {
        if (id == null) {
            return null;
        }
        return video.id.eq(id);
    }

    private BooleanExpression eqChannelId(Long channelId) {
        if (channelId == null) {
            return null;
        }
        return video.channelId.eq(channelId);
    }

    private BooleanExpression eqYoutubeId(String youtubeId) {
        if (youtubeId == null) {
            return null;
        }
        return video.youtubeId.eq(youtubeId);
    }

    private BooleanExpression likeTitle(String title) {
        if (title == null) {
            return null;
        }
        return video.title.likeIgnoreCase("%" + title + "%");
    }
}
