package demo.playground.reactivespringauth


import demo.playground.reactivespringauth.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono

class AuthControllerSpec extends BaseSpecification {

    @Autowired
    UserRepository userRepository;

    def setup() {
        userRepository.deleteAll().block();
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
}
