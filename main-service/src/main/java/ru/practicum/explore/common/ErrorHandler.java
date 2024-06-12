package ru.practicum.explore.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class ErrorHandler {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(final NotFoundException e) {
        log.error("404 Не найден id: {}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("status", "NOT_FOUND",
                        "reason", "The required object was not found.",
                        "message", e.getMessage(),
                        "timestamp", LocalDateTime.now().format(formatter)),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(NotValidEventDateException.class)
    public ResponseEntity<Map<String, String>> handleNotValidEventDateException(final NotValidEventDateException e) {
        log.error("400 Запрос составлен некорректно");
        return new ResponseEntity<>(
                Map.of("status", "FORBIDDEN",
                        "reason", "For the requested operation the conditions are not met.",
                        "message", e.getMessage(),
                        "timestamp", LocalDateTime.now().format(formatter)),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(IncorrectRequestException.class)
    public ResponseEntity<Map<String, String>> handleIncorrectRequestException(final IncorrectRequestException e) {
        log.error("400 Запрос составлен некорректно");
        return new ResponseEntity<>(
                Map.of("status", "BAD_REQUEST",
                        "reason", "Incorrectly made request.",
                        "message", e.getMessage(),
                        "timestamp", LocalDateTime.now().format(formatter)),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleSQLIntegrityConstraintViolationException(
            final SQLIntegrityConstraintViolationException e) {
        log.error("409 Нарушение уникального индекса");
        return new ResponseEntity<>(
                Map.of("status", "CONFLICT",
                        "reason", "Incorrectly made request.",
                        "message", e.getMessage(),
                        "timestamp", LocalDateTime.now().format(formatter)),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, String>> handleConflictException(
            final ConflictException e) {
        log.error("409 Нарушение уникального индекса");
        return new ResponseEntity<>(
                Map.of("status", "CONFLICT",
                        "reason", "Incorrectly made request.",
                        "message", e.getMessage(),
                        "timestamp", LocalDateTime.now().format(formatter)),
                HttpStatus.CONFLICT
        );
    }
}
