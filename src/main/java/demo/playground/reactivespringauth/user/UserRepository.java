package demo.playground.reactivespringauth.user;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<AuthUser, String> {

    Mono<AuthUser> findByEmail(String email);

}
