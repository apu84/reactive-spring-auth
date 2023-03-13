package demo.playground.reactivespringauth.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
public class AuthUser {

    @Id
    String id;

    String username;

    @JsonIgnore
    String password;

    String email;

    boolean accountExpired ;
    boolean credentialExpired;
    boolean disabled;
    boolean accountLocked;

    List<String> roles = new ArrayList<>();

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public boolean isAccountExpired() {
        return accountExpired;
    }

    public boolean isCredentialExpired() {
        return credentialExpired;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }
}
