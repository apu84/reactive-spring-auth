package demo.playground.reactivespringauth

import demo.playground.reactivespringauth.api.AuthController
import demo.playground.reactivespringauth.security.jwt.TokenRepository
import demo.playground.reactivespringauth.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import reactor.core.publisher.Mono

@WebFluxTest(AuthController.class)
class AuthControllerSpec extends BaseSpecification {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenRepository tokenRepository;

    def setup() {
        userRepository.deleteAll().block()
        tokenRepository.deleteAll().block()
    }

    def "GET /auth/login should return 405"() {

    expect:
    webTestClient.get()
        .uri("/auth/login")
        .exchange()
        .expectStatus().isEqualTo(405)
    }

    def "POSTing a new user should return 201"() {
        expect:
        webTestClient.post()
        .uri("/auth/new-user")
        .header("Content-Type", "application/json")
        .body(Mono.just("{\"username\":\"test\", \"password\":\"test\", \"email\": \"test@test.com\"}"), String.class)
        .exchange()
        .expectStatus().isCreated()
    }

    def "POSTing existing user should return 400"() {
        given: "User Already exists"
        webTestClient.post()
                .uri("/auth/new-user")
                .header("Content-Type", "application/json")
                .body(Mono.just("{\"username\":\"test\", \"password\":\"test\", \"email\": \"test@test.com\"}"), String.class)
                .exchange()

        when:
        var response =  webTestClient.post()
                .uri("/auth/new-user")
                .header("Content-Type", "application/json")
                .body(Mono.just("{\"username\":\"test\", \"password\":\"test\", \"email\": \"test@test.com\"}"), String.class)
                .exchange()

        then:
        response.expectStatus().isBadRequest()
    }

    def "Login using wrong username/password will give 401"() {
        given: "A new user with name test1 and password test1 is created"
            webTestClient.post()
                .uri("/auth/new-user")
                .header("Content-Type", "application/json")
                .body(Mono.just("{\"username\":\"test1\", \"password\":\"test1\", \"email\": \"test1@test.com\"}"), String.class)
                .exchange()

        when: "User login with username:test1@test.com and password: test"
            var response = webTestClient.post()
                .uri("/auth/login")
                .header("Content-Type", "application/json")
                .body(Mono.just("{\"username\":\"test1@test.com\", \"password\":\"test\"}"), String.class)
                .exchange()
        then: "An Unauthorized response is generated"
            response.expectStatus().isUnauthorized()
    }

    def "Login using right username/password will give 200"() {
        given: "A new user with name test1 and password test1 is created"
        webTestClient.post()
                .uri("/auth/new-user")
                .header("Content-Type", "application/json")
                .body(Mono.just("{\"username\":\"test1\", \"password\":\"test1\", \"email\": \"test1@test.com\"}"), String.class)
                .exchange()

        when: "User login with username:test1@test.com and password: test1"
        var response = webTestClient.post()
                .uri("/auth/login")
                .header("Content-Type", "application/json")
                .body(Mono.just("{\"username\":\"test1@test.com\", \"password\":\"test1\"}"), String.class)
                .exchange()
        then: "An Ok response is generated, it contains a String access_token"
        response.expectStatus().isOk()
                .expectBody(String.class)
    }

    def "/me endpoint will give valid json with username and roles"() {
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

        Map<String,String> body = responseBody.responseBody.blockFirst();
        var accessToken = body.get("access_token")

        when: "/auth/me request is done with the returned access token"
        var meResponse = webTestClient.get()
                .uri("/auth/me")
                .header("Authorization", "Bearer "+ accessToken)
                .exchange()


        then:
            meResponse.expectStatus().isOk()
            .expectBody()
            .jsonPath("\$.username").isEqualTo("test1")
            .jsonPath("\$.email").isEqualTo("test1@test.com")
            .jsonPath("\$.roles").isArray()
            .jsonPath("\$.roles.size()").isEqualTo(1)
            .jsonPath("\$.roles[0]").isEqualTo("USER")

    }
}
