package com.edu.university.builders;

import com.edu.university.modules.auth.entity.Users;
import java.util.UUID;

public class UsersBuilder {
    private String username = "user_" + UUID.randomUUID().toString().substring(0, 8);
    private String email = username + "@example.com";
    private String password = "{noop}password"; // Standard test password

    public static UsersBuilder aUser() {
        return new UsersBuilder();
    }

    public UsersBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public UsersBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UsersBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public Users build() {
        return Users.builder()
                .username(username)
                .email(email)
                .password(password)
                .isActive(true)
                .emailVerified(true)
                .build();
    }
}
