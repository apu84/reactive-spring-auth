package demo.playground.reactivespringauth;

import demo.playground.reactivespringauth.domain.User;
import demo.playground.reactivespringauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInit {
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public DataInit(UserRepository userRepository,
                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public void init() {
        Flux.just("user", "admin")
                .flatMap(username -> {
                    System.out.println(username);
                    List<String> roles = "user".equals(username) ?
                            Arrays.asList("ROLE_USER") : Arrays.asList("ROLE_USER", "ROLE_ADMIN");

                    User user = User.builder()
                            .roles(roles)
                            .username(username)
                            .password(passwordEncoder.encode("password"))
                            .email(username + "@example.com")
                            .build();
                    System.out.println(user);
                    return this.userRepository.save(user);
                });
    }
}
