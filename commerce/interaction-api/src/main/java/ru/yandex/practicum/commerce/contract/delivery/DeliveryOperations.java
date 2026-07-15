package ru.yandex.practicum.commerce.contract.delivery;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.shopping.order.OrderDto;
import ru.yandex.practicum.commerce.exception.NoDeliveryFoundException;

import java.util.UUID;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryOperations {
    @PutMapping
    DeliveryDto createNewDelivery(@RequestBody DeliveryDto deliveryDto);

    @PostMapping("/successful/{orderId}")
    void deliverySuccess(@PathVariable UUID orderId) throws NoDeliveryFoundException;

    @PostMapping("/picked/{orderId}")
    void deliveryPicked(@PathVariable UUID orderId) throws NoDeliveryFoundException;

    @PostMapping("/failed/{orderId}")
    void deliveryFail(@PathVariable UUID orderId) throws NoDeliveryFoundException;

    @PostMapping("/cost")
    Double deliveryCostCalc(@RequestBody OrderDto orderDto) throws NoDeliveryFoundException;
}
