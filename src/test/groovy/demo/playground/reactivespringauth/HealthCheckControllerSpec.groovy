package demo.playground.reactivespringauth

import demo.playground.reactivespringauth.api.HealthCheck
import demo.playground.reactivespringauth.config.AppConfig
import demo.playground.reactivespringauth.config.SecurityConfig
import demo.playground.reactivespringauth.security.jwt.JwtProperties
import demo.playground.reactivespringauth.security.jwt.JwtTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Component
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import spock.lang.Specification

@ContextConfiguration()
@WebFluxTest(HealthCheck.class)
@AutoConfigureDataMongo
@ComponentScan(["demo.playground.reactivespringauth"])
class HealthCheckControllerSpec extends Specification {
    def PING_URL_PATH = "/health-check/ping"

    @Autowired
    private WebTestClient webTestClient;

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
