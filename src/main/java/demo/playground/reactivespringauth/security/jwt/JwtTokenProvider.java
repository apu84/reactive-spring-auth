package demo.playground.reactivespringauth.security.jwt;

import demo.playground.reactivespringauth.user.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.joining;

@Component
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "roles";

    private final JwtProperties jwtProperties;

    private final SecretKey secretKey;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Autowired
    public JwtTokenProvider(final JwtProperties jwtProperties,
                            final TokenRepository tokenRepository,
                            final UserRepository userRepository) {
        this.jwtProperties = jwtProperties;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        var secret = Base64.getEncoder()
                .encodeToString(this.jwtProperties.getSecretKey().getBytes());
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Mono<String> createToken(Authentication authentication) {

        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication
                .getAuthorities();
        Claims claims = Jwts.claims().setSubject(username);
        if (!authorities.isEmpty()) {
            claims.put(AUTHORITIES_KEY, authorities.stream()
                    .map(GrantedAuthority::getAuthority).collect(joining(",")));
        }

        Date now = new Date();
        Date validity = new Date(now.getTime() + this.jwtProperties.getValidityInMs());
        String token = Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(validity)
                .signWith(this.secretKey, SignatureAlgorithm.HS256).compact();
        return userRepository.findByUsername(username)
                .flatMap((user -> tokenRepository
                        .save(new Token(token, user.getId()))
                        .thenReturn(token)));
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(this.secretKey).build()
                .parseClaimsJws(token).getBody();

        Object authoritiesClaim = claims.get(AUTHORITIES_KEY);

        Collection<? extends GrantedAuthority> authorities = authoritiesClaim == null
                ? AuthorityUtils.NO_AUTHORITIES
                : AuthorityUtils
                .commaSeparatedStringToAuthorityList(authoritiesClaim.toString());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public Mono<Boolean> validateToken(String token) {
        return isTokenPresent(token)
                .flatMap(isPresent -> {
                    if (!isPresent) {
                        return Mono.just(false);
                    }
                    try {
                        Jwts.parserBuilder().setSigningKey(this.secretKey)
                                .build().parseClaimsJws(token);
                        return Mono.just(true);
                    } catch (JwtException | IllegalArgumentException e) {
                        return deactivateToken(token).thenReturn(false);
                    }
                });
    }

    public Mono<Token> deactivateToken(final String token) {
        return tokenRepository.findByContent(token)
                .flatMap(t -> {
                    t.deActivate();
                    return tokenRepository.save(t);
                });
    }

    private Mono<Boolean> isTokenPresent(final String token) {
        return tokenRepository.findByContent(token)
                .map(Token::isActive)
                .switchIfEmpty(Mono.just(false));
    }
}