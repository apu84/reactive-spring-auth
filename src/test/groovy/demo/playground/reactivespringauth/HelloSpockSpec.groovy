package demo.playground.reactivespringauth

import spock.lang.Specification

class HelloSpockSpec extends Specification {
    def "length of #name and his friends' names"() {
        expect:
        name.size() == length

        where:
        name     | length
        "Spock"  | 5
        "Kirk"   | 4
        "Scotty" | 6
    }
}
