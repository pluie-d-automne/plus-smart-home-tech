package ru.yandex.practicum.commerce;

import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.shopping.order.OrderDto;
import ru.yandex.practicum.commerce.exception.NoDeliveryFoundException;

import java.util.UUID;

public interface DeliveryService {
    DeliveryDto createNewDelivery(DeliveryDto deliveryDto);

    void deliverySuccess(UUID orderId) throws NoDeliveryFoundException;

    void deliveryPicked(UUID orderId) throws NoDeliveryFoundException;

    void deliveryFail(UUID orderId) throws NoDeliveryFoundException;

    Double deliveryCostCalc(OrderDto orderDto) throws NoDeliveryFoundException;
}
