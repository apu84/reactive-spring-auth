package demo.playground.reactivespringauth.security.jwt;

import org.springframework.data.annotation.Id;

import java.util.Date;

public class Token {
    @Id
    private String id;
    private String content;
    private String userId;
    private Date created;
    private boolean active;

    public Token(String content, String userId) {
        this.content = content;
        this.userId = userId;
        this.created = new Date();
        this.active = true;
    }

    public void deActivate() {
        this.active = false;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getUserId() {
        return userId;
    }

    public Date getCreated() {
        return created;
    }

    public boolean isActive() {
        return active;
    }
}
