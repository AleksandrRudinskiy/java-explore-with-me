package ru.practicum.explore.common;

public class NotCorrectDataException extends RuntimeException {
    public NotCorrectDataException(String message) {
        super(message);
    }
}
