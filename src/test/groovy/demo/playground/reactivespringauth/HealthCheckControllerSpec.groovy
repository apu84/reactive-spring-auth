package demo.playground.reactivespringauth

import demo.playground.reactivespringauth.api.HealthCheck
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest

@WebFluxTest(HealthCheck.class)
class HealthCheckControllerSpec extends BaseSpecification {
    def PING_URL_PATH = "/health-check/ping"

    def "Simple test to make sure Spock is working: 1 + 1 = 2"() {
        when:
        int one = 1

        then:
        assert one + one == 2

    }

    def "Invoking /ping should return pong with 200"() {
        expect:
            webTestClient.get()
                .uri(PING_URL_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("pong")
    }

}
