package demo.playground.reactivespringauth.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth/health-check")
public class HealthCheck {
    @GetMapping("/ping")
    public Mono<String> ping() {
        return Mono.just("pong");
    }
}
