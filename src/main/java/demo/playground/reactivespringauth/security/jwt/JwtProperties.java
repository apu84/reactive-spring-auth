package demo.playground.reactivespringauth.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secretkey;

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
