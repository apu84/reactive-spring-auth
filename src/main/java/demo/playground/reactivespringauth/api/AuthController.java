package demo.playground.reactivespringauth.api;


import demo.playground.reactivespringauth.user.User;
import demo.playground.reactivespringauth.user.UserRepository;
import demo.playground.reactivespringauth.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtTokenProvider tokenProvider;

    private final ReactiveAuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private static final String BEARER = "Bearer ";
    private static final Predicate<String> matchBearerLength = authValue -> authValue.length() > BEARER.length();
    private static final Function<String, Mono<String>> isolateBearerValue = authValue -> Mono.justOrEmpty(authValue.substring(BEARER.length()));


    @Autowired
    public AuthController(final JwtTokenProvider jwtTokenProvider,
                          final ReactiveAuthenticationManager reactiveAuthenticationManager,
                          final PasswordEncoder passwordEncoder,
                          final UserRepository userRepository) {
        this.tokenProvider = jwtTokenProvider;
        this.authenticationManager = reactiveAuthenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity> login(
            @RequestBody Mono<User> authRequest) {
        return authRequest
                .flatMap(login -> this.authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(
                                login.getUsername(), login.getPassword()))
                        .flatMap(this.tokenProvider::createToken)
                )
                .map(jwt -> {
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
                    var tokenBody = Map.of("access_token", jwt);
                    return new ResponseEntity<>(tokenBody, httpHeaders, HttpStatus.OK);
                });
    }

    @PostMapping("new-user")
    public Mono<ResponseEntity<User>> create(@RequestBody Mono<User> userEntity) {
        return userEntity
                .map(user -> User.builder()
                        .username(user.getUsername())
                        .password(passwordEncoder.encode(user.getPassword()))
                        .roles(List.of("USER"))
                        .build())
                .flatMap(userRepository::save)
                .map(savedUser -> new ResponseEntity<>(savedUser, HttpStatus.CREATED));
    }

    @GetMapping("/me")
    public Mono<Map<String, Object>> currentUser(@AuthenticationPrincipal Mono<UserDetails> principal) {
        return principal.map(user -> Map.of(
                "userName", user.getUsername(),
                "roles", AuthorityUtils.authorityListToSet(user.getAuthorities())));
    }

    @PostMapping("/logout")
    public Mono<Void> logout(@RequestHeader("Authorization") String authorization) {
        return Mono.just(authorization)
                .filter(matchBearerLength)
                .flatMap(isolateBearerValue)
                .flatMap(token -> tokenProvider
                        .deactivateToken(token)
                        .thenEmpty(Mono.empty()));
    }

}
