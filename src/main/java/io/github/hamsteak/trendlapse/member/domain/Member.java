package io.github.hamsteak.trendlapse.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "username"))
    private Username username;

    @Column(nullable = false)
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "password"))
    private Password password;

    @Column
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email"))
    private Email email;

    public Member(Long id, Username username, Password password, Email email) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        Objects.requireNonNull(email);

        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public void changeUsername(Username username) {
        Objects.requireNonNull(username);
        this.username = username;
    }

    public void changePassword(Password password) {
        Objects.requireNonNull(password);
        this.password = password;
    }

    public void changeEmail(Email email) {
        Objects.requireNonNull(email);
        this.email = email;
    }
}
