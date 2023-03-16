package demo.playground.reactivespringauth.config;

import demo.playground.reactivespringauth.security.jwt.*;
import demo.playground.reactivespringauth.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private UserRepository userRepository;

    @Autowired
    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http,
                                                JwtTokenProvider tokenProvider) {
        //Disable things you don't need in spring security.
        http.httpBasic().disable();
        http.formLogin().disable();
        http.csrf().disable();
        http.logout().disable();

        //Those that do not require jwt token authentication should be pass.
        http.authorizeExchange()
                .pathMatchers("/auth/health-check/**", "/auth/login", "/auth/new-user")
                .permitAll();

        http.authorizeExchange()
                .pathMatchers("/user/{user}").access(this::currentUserMatchesPath)
                .pathMatchers("/**").authenticated()
                .and()
                .addFilterAt(bearerAuthenticationFilter(tokenProvider), SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .cors();

        return http.build();
    }

    private AuthenticationWebFilter bearerAuthenticationFilter(final JwtTokenProvider jwtTokenProvider) {
        AuthenticationWebFilter bearerAuthenticationFilter = new AuthenticationWebFilter((ReactiveAuthenticationManager) Mono::just);
        ServerAuthenticationConverter bearerConverter = new JwtToAuthConverter(jwtTokenProvider, userDetailsService());
        bearerAuthenticationFilter.setServerAuthenticationConverter(bearerConverter);
        bearerAuthenticationFilter.setRequiresAuthenticationMatcher(pathMatchers("/auth/me", "/user/**", "/auth/logout"));
        return bearerAuthenticationFilter;
    }

    private Mono<AuthorizationDecision> currentUserMatchesPath(Mono<Authentication> authentication,
                                                               AuthorizationContext context) {
        return authentication
                .map(a -> context.getVariables().get("user").equals(a.getName()))
                .map(AuthorizationDecision::new);
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .map(user -> User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities(user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(toList()))
                        .accountExpired(user.isAccountExpired())
                        .credentialsExpired(user.isCredentialExpired())
                        .disabled(user.isDisabled())
                        .accountLocked(user.isAccountLocked())
                        .build()
                );
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService,
                                                                       PasswordEncoder passwordEncoder) {
        var authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }

}
