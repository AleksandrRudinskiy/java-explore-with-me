package ru.practicum;

public class NotCorrectDataException extends RuntimeException {
    public NotCorrectDataException(String message) {
        super(message);
    }
}
