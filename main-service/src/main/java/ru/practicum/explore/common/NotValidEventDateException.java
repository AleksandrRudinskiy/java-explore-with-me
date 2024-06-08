package ru.practicum.explore.common;

public class NotValidEventDateException extends RuntimeException{
    public NotValidEventDateException(String message) {
        super(message);
    }
}
