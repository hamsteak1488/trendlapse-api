package io.github.hamsteak.trendlapse.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @AttributeOverride(name = "hashValue", column = @Column(name = "password_hash"))
    private Password password;

    @Column
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email"))
    private Email email;

    public Member(Long id, String username, String passwordHash, String email) {
        this.id = id;
        this.username = Username.of(username);
        this.password = Password.of(passwordHash);
        this.email = email != null ? Email.of(email) : null;
    }

    public void changeUsername(String username) {
        this.username = Username.of(username);
    }

    public void changePassword(String passwordHash) {
        this.password = Password.of(passwordHash);
    }

    public void changeEmail(String email) {
        this.email = Email.of(email);
    }
}
