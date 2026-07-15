package ru.yandex.practicum.commerce.contract.shopping.payment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.dto.shopping.order.OrderDto;
import ru.yandex.practicum.commerce.dto.shopping.order.PaymentDto;
import ru.yandex.practicum.commerce.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.exception.NotEnoughInfoInOrderToCalculateException;

import java.util.UUID;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentOperations {
    @PostMapping
    PaymentDto createPayment(@RequestBody OrderDto orderDto) throws NotEnoughInfoInOrderToCalculateException;

    @PostMapping("/totalCost")
    Double calcTotalCost(@RequestBody OrderDto orderDto) throws NotEnoughInfoInOrderToCalculateException;

    @PostMapping("/productCost")
    Double calcProductCost(@RequestBody OrderDto orderDto) throws NotEnoughInfoInOrderToCalculateException;

    @PostMapping("/refund")
    void refund(@PathVariable UUID paymentId) throws NoOrderFoundException;

    @PostMapping("/failed")
    void failPayment(@PathVariable UUID paymentId) throws NoOrderFoundException;
}
