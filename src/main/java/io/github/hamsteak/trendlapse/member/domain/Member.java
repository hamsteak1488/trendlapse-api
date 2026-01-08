package io.github.hamsteak.trendlapse.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String email;

    public Member(Long id, String username, String password, String email) {
        validateUsername(username);
        validatePassword(password);
        validateEmail(email);

        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public void changeUsername(String username) {
        validateUsername(username);
        this.username = username;
    }

    public void changePassword(String password) {
        validatePassword(password);
        this.password = password;
    }

    public void changeEmail(String email) {
        validateEmail(email);
        this.email = email;
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new InvalidUsernameException("Username must not be blank.");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new InvalidPasswordException("Password must not be blank.");
        }
    }

    private void validateEmail(String email) {
        if (email == null) {
            return;
        }

        if (email.isBlank()) {
            throw new InvalidEmailException("Email must not be blank.");
        }

        if (!EmailValidator.getInstance().isValid(email)) {
            throw new InvalidEmailException("Email is invalid.");
        }
    }
}
