package demo.playground.reactivespringauth.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("health-check")
public class HealthCheck {
    @GetMapping("/ping")
    Mono<String> ping() {
        return Mono.just("pong");
    }
}
