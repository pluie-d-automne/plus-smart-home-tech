package ru.yandex.practicum.commerce.exception;

public class ServiceTemporaryUnavailable extends RuntimeException {
    public ServiceTemporaryUnavailable(String message) {
        super(message);
    }
}
