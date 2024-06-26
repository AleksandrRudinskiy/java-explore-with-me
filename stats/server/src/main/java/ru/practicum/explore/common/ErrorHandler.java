package ru.practicum.explore.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(NotCorrectDataException.class)
    public ResponseEntity<Map<String, String>> handleNotCorrectDataException(final NotCorrectDataException e) {
        log.error("400 : {}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

}
