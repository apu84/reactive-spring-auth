package demo.playground.reactivespringauth

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
