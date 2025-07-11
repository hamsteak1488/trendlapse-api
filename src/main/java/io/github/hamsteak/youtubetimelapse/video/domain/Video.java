package io.github.hamsteak.youtubetimelapse.video.domain;

import io.github.hamsteak.youtubetimelapse.channel.domain.Channel;
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

    @Column
    @NotNull
    private String youtubeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    @NotNull
    private Channel channel;

    @Column
    @NotNull
    private String title;

    @Column
    @NotNull
    private String thumbnailUrl;
}
