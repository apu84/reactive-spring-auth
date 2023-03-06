package demo.playground.reactivespringauth.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
public class User {

    @Id
    String id;

    String username;

    @JsonIgnore
    String password;

    String email;

    boolean active = true;

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

    public boolean isActive() {
        return active;
    }

    public List<String> getRoles() {
        return roles;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }
}