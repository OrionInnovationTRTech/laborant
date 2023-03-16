package tr.com.orioninc.laborant.exception.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tr.com.orioninc.laborant.exception.custom.AlreadyExistsException;
import tr.com.orioninc.laborant.exception.custom.NotAuthorizedException;
import tr.com.orioninc.laborant.exception.custom.NotConnectedException;
import tr.com.orioninc.laborant.exception.custom.NotFoundException;

import java.time.LocalDateTime;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> handleNotFound(NotFoundException e, WebRequest request) {
        return new ResponseEntity<>(new ApiErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, LocalDateTime.now()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleBadRequest(AlreadyExistsException e, WebRequest request) {
        return new ResponseEntity<>(new ApiErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<Object> handleNotConnected(NotConnectedException e, WebRequest request) {
        return new ResponseEntity<>(new ApiErrorResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleNotAuthorized(NotAuthorizedException e, WebRequest request) {
        return new ResponseEntity<>(new ApiErrorResponse(e.getMessage(),HttpStatus.UNAUTHORIZED, LocalDateTime.now()), HttpStatus.UNAUTHORIZED);
    }
}
