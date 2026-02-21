package io.github.hamsteak.trendlapse.ai.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Prompt {
    @Id
    private String id;

    @Lob
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column
    @NotNull
    @Setter
    private String content;
}
