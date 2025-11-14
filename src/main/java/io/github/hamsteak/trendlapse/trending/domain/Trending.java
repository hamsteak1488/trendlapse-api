package io.github.hamsteak.trendlapse.trending.domain;

import io.github.hamsteak.trendlapse.region.domain.Region;
import io.github.hamsteak.trendlapse.video.domain.Video;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Trending {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull
    private LocalDateTime dateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    @NotNull
    private Video video;

    @Column
    @NotNull
    private Integer rankValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    @NotNull
    private Region region;
}
