package demo.playground.reactivespringauth.user;

import org.springframework.util.Assert;

import java.util.List;

import static org.springframework.util.Assert.notNull;

public class UserBuilder {
    private final AuthUser user;

    public UserBuilder() {
        this.user = new AuthUser();
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

    public UserBuilder accountLocked(boolean accountLocked) {
        this.user.accountLocked = accountLocked;
        return this;
    }

    public UserBuilder disabled(boolean disabled) {
        this.user.disabled = disabled;
        return this;
    }

    public UserBuilder accountExpired(boolean accountExpired) {
        this.user.accountExpired = accountExpired;
        return this;
    }

    public UserBuilder passwordExpired(boolean passwordExpired) {
        this.user.credentialExpired = passwordExpired;
        return this;
    }
    public AuthUser build() {
        notNull(this.user.email, "User email is required");
        notNull(this.user.password, "User password is required");
        return this.user;
    }

}
