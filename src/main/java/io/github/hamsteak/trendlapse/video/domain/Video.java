package io.github.hamsteak.trendlapse.video.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
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

    @NotNull
    private Long channelId;

    @Column
    @NotNull
    private String title;

    @Column
    private String thumbnailUrl;

    @Column
    @LastModifiedDate
    private LocalDateTime lastUpdatedAt;
}
