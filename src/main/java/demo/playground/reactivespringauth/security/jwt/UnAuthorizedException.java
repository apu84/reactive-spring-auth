package demo.playground.reactivespringauth.security.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnAuthorizedException extends RuntimeException {
    private final String message;

    public UnAuthorizedException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
