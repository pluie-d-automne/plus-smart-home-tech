package ru.yandex.practicum.commerce.exception;

public class NoWarehouseFound extends RuntimeException {
    public NoWarehouseFound(String message) {
        super(message);
    }
}
