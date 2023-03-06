package demo.playground.reactivespringauth.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secretKey = "rzxlszyykpbgqcflzxsqcysyhljt";

    // validity in milliseconds
    private long validityInMs = 3600000; // 1h

    public String getSecretKey() {
        return secretKey;
    }

    public long getValidityInMs() {
        return validityInMs;
    }
}
