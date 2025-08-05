package io.github.hamsteak.trendlapse.video.domain;

import io.github.hamsteak.trendlapse.channel.domain.Channel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull
    private String youtubeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    @NotNull
    private Channel channel;

    @Column
    @NotNull
    private String title;

    @Column(nullable = true)
    private String thumbnailUrl;
}
