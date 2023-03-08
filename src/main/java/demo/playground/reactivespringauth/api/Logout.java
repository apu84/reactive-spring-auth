package demo.playground.reactivespringauth.api;

import demo.playground.reactivespringauth.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
public class Logout {
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public Logout(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity> logout(@RequestHeader("Authorization") String authorization) {
        return jwtTokenProvider.deactivateToken(authorization)
                .thenReturn(new ResponseEntity(HttpStatus.OK));
    }
}
