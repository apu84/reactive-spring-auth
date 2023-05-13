package demo.playground.reactivespringauth

import demo.playground.reactivespringauth.api.AuthController
import demo.playground.reactivespringauth.security.jwt.TokenRepository
import demo.playground.reactivespringauth.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import reactor.core.publisher.Mono

@WebFluxTest(
        controllers=AuthController.class,
        properties=["jwt.secretkey=123456789012345678901234567890","jwt.validityinms=1"]
)
@Testcontainers
class AuthController2Spec extends BaseSpecification {
    @Autowired
    UserRepository userRepository

    @Autowired
    TokenRepository tokenRepository

    final static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"))

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        mongoDBContainer.start()
        registry.add("spring.data.mongodb.uri", () ->   mongoDBContainer.replicaSetUrl)
    }

    def "Request with an expired token will return 401"() {
        given: "A new user with name test1 and password test1 is created"
        webTestClient.post()
                .uri("/auth/new-user")
                .header("Content-Type", "application/json")
                .body(Mono.just("{\"username\":\"test1\", \"password\":\"test1\", \"email\": \"test1@test.com\"}"), String.class)
                .exchange()

        and: "User login with username:test1@test.com and password: test1"
        var responseBody = webTestClient.post()
                .uri("/auth/login")
                .header("Content-Type", "application/json")
                .body(Mono.just("{\"username\":\"test1@test.com\", \"password\":\"test1\"}"), String.class)
                .exchange()
                .returnResult(Map.class)

        Map<String, String> body = responseBody.responseBody.blockFirst();
        var accessToken = body.get("access_token")

        when: "/auth/me request is done with the returned access token"
        var meResponse = webTestClient.get()
                .uri("/auth/me")
                .header("Authorization", "Bearer " + accessToken)
                .exchange()

        then:
        meResponse.expectStatus().isUnauthorized()
    }

    def cleanup() {
        userRepository.deleteAll().block()
        tokenRepository.deleteAll().block()
    }
}
