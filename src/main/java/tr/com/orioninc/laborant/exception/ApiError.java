package tr.com.orioninc.laborant.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ApiError {
    private String message;
    private HttpStatus httpStatus;
    private LocalDateTime timestamp;

    public ApiError(String message, LocalDateTime timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }
}
