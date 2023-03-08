package demo.playground.reactivespringauth.security.jwt;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface TokenRepository extends ReactiveMongoRepository<Token, String> {
    Mono<Token> findByContent(String content);
}
