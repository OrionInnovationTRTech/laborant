package tr.com.orioninc.laborant.exception.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ApiErrorResponse {
    private String message;
    private HttpStatus httpStatus;
    private LocalDateTime timestamp;

    public ApiErrorResponse(String message, LocalDateTime timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }
}
