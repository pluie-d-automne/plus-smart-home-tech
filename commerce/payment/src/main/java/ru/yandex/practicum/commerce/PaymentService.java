package ru.yandex.practicum.commerce;

import ru.yandex.practicum.commerce.dto.shopping.order.OrderDto;
import ru.yandex.practicum.commerce.dto.shopping.order.PaymentDto;
import ru.yandex.practicum.commerce.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.exception.NotEnoughInfoInOrderToCalculateException;

import java.util.UUID;

public interface PaymentService {
    PaymentDto createPayment(OrderDto orderDto) throws NotEnoughInfoInOrderToCalculateException;

    Double calcTotalCost(OrderDto orderDto) throws NotEnoughInfoInOrderToCalculateException;

    Double calcProductCost(OrderDto orderDto) throws NotEnoughInfoInOrderToCalculateException;

    void refund(UUID paymentId) throws NoOrderFoundException;

    void failPayment(UUID paymentId) throws NoOrderFoundException;
}
