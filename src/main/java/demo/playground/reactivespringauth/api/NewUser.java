package demo.playground.reactivespringauth.api;

import demo.playground.reactivespringauth.domain.User;
import demo.playground.reactivespringauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/new-user")
public class NewUser {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @Autowired
    public NewUser(UserRepository userRepository,
                   PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public Mono<ResponseEntity> create(@RequestBody Mono<AuthenticationRequest> userEntity) {
       return userEntity
               .map(user -> User.builder()
                       .username(user.getUsername())
                       .password(passwordEncoder.encode(user.getPassword()))
                       .roles(List.of("USER"))
                       .build())
               .flatMap(user -> userRepository.save(user))
               .map(savedUser -> new ResponseEntity<>(savedUser, HttpStatus.CREATED));
    }
}
