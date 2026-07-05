package ru.yandex.practicum.commerce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.shopping.payment.PaymentOperations;
import ru.yandex.practicum.commerce.dto.shopping.order.OrderDto;
import ru.yandex.practicum.commerce.dto.shopping.order.PaymentDto;
import ru.yandex.practicum.commerce.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.exception.NotEnoughInfoInOrderToCalculateException;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController implements PaymentOperations {
    private final PaymentService paymentService;

    @Override
    @PostMapping
    public PaymentDto createPayment(@RequestBody OrderDto orderDto) throws NotEnoughInfoInOrderToCalculateException {
        log.info("Need to create payment for {}", orderDto);
        PaymentDto paymentDto = paymentService.createPayment(orderDto);
        log.info("Payment created: {}", paymentDto);
        return paymentDto;
    }

    @Override
    @PostMapping("/totalCost")
    public Double calcTotalCost(@RequestBody OrderDto orderDto) throws NotEnoughInfoInOrderToCalculateException {
        log.info("Need to calculate totalCost for order: {}", orderDto);
        Double totalCost = paymentService.calcTotalCost(orderDto);
        log.info("totalCost calculated: {}", totalCost);
        return totalCost;
    }

    @Override
    @PostMapping("/productCost")
    public Double calcProductCost(@RequestBody OrderDto orderDto) throws NotEnoughInfoInOrderToCalculateException {
        log.info("Need to calculate productCost for order: {}", orderDto);
        Double productCost = paymentService.calcProductCost(orderDto);
        log.info("productCost calculated: {}", productCost);
        return productCost;
    }

    @Override
    @PostMapping("/refund")
    public void refund(@PathVariable UUID paymentId) throws NoOrderFoundException {
        log.info("Need to update payment {} as successful", paymentId);
        paymentService.refund(paymentId);
    }

    @Override
    @PostMapping("/failed")
    public void failPayment(@PathVariable UUID paymentId) throws NoOrderFoundException {
        log.info("Need to update payment {} as failed", paymentId);
        paymentService.failPayment(paymentId);
    }
}
