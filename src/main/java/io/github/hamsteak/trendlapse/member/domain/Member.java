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
    @AttributeOverride(name = "value", column = @Column(name = "password"))
    private Password password;

    @Column
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email"))
    private Email email;

    public Member(Long id, String username, String password, String email) {
        this.id = id;
        this.username = Username.of(username);
        this.password = Password.of(password);
        this.email = Email.of(email);
    }

    public void changeUsername(String username) {
        this.username = Username.of(username);
    }

    public void changePassword(String password) {
        this.password = Password.of(password);
    }

    public void changeEmail(String email) {
        this.email = Email.of(email);
    }
}
