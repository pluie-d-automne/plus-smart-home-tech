package ru.yandex.practicum.commerce.dto.shopping.product;

public enum QuantityState {
    ENDED, // товар закончился
    FEW, // осталось меньше 10 единиц товара
    ENOUGH, // осталось от 10 до 100 единиц
    MANY // осталось больше 100 единиц
}
