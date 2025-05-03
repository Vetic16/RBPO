package ru.mtuci.rbpo_2024_praktika.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import ru.mtuci.rbpo_2024_praktika.exception.JwtTokenException;
import ru.mtuci.rbpo_2024_praktika.exception.InvalidSessionException;
import ru.mtuci.rbpo_2024_praktika.exception.MissingTokenTypeException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JwtTokenException.class)
    public ResponseEntity<String> handleJwtTokenException(JwtTokenException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidSessionException.class)
    public ResponseEntity<String> handleInvalidSessionException(InvalidSessionException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MissingTokenTypeException.class)
    public ResponseEntity<String> handleMissingTokenTypeException(MissingTokenTypeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
