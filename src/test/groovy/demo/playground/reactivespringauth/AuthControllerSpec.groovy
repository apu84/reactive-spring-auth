package demo.playground.reactivespringauth

import demo.playground.reactivespringauth.api.AuthController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import spock.lang.Specification

@ContextConfiguration()
@WebFluxTest(AuthController.class)
@AutoConfigureDataMongo
@ComponentScan(["demo.playground.reactivespringauth"])
class AuthControllerSpec extends Specification {
    @Autowired
    WebTestClient webTestClient;

    def "GET /auth/login should return 405"() {

    expect:
    webTestClient.get()
        .uri("/auth/login")
        .exchange()
        .expectStatus().isEqualTo(405)
    }
}
