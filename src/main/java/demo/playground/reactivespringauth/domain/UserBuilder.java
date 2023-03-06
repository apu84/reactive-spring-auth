package demo.playground.reactivespringauth.domain;

import java.util.ArrayList;
import java.util.List;

public class UserBuilder {
    private User user;

    public UserBuilder() {
        this.user = new User();
    }

    public UserBuilder roles(List<String> role) {
        this.user.roles = role;
        return this;
    }

    public UserBuilder username(String username) {
        this.user.username = username;
        return this;
    }

    public UserBuilder password(String password) {
        this.user.password = password;
        return this;
    }

    public UserBuilder email(String email) {
        this.user.email = email;
        return this;
    }

    public User build() {
        return this.user;
    }

}
