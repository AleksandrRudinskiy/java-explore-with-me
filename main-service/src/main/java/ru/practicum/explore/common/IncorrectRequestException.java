package ru.practicum.explore.common;

public class IncorrectRequestException extends RuntimeException{
    public IncorrectRequestException(String message) {
        super(message);
    }
}
