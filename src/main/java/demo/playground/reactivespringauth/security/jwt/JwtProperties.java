package demo.playground.reactivespringauth.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "jwt")
@Component
public class JwtProperties {

    private String secretkey = "test";

    // validity in milliseconds
    private long validityinms; // 1h

    public String getSecretkey() {
        return secretkey;
    }

    public long getValidityinms() {
        return validityinms;
    }

    public void setSecretkey(String secretkey) {
        this.secretkey = secretkey;
    }

    public void setValidityinms(long validityinms) {
        this.validityinms = validityinms;
    }
}
