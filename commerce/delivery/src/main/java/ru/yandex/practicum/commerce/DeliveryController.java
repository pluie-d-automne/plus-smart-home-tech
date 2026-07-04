package ru.yandex.practicum.commerce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.delivery.DeliveryOperations;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.shopping.order.OrderDto;
import ru.yandex.practicum.commerce.exception.NoDeliveryFoundException;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryOperations {
    private final DeliveryService deliveryService;

    @Override
    @PutMapping
    public DeliveryDto createNewDelivery(@RequestBody DeliveryDto deliveryDto) {
        log.info("Need to create new delivery: {}", deliveryDto);
        DeliveryDto deliveryCreated = deliveryService.createNewDelivery(deliveryDto);
        log.info("New delivery created: {}", deliveryCreated);
        return deliveryCreated;
    }

    @Override
    @PostMapping("/successful/{orderId}")
    public void deliverySuccess(@PathVariable UUID orderId) throws NoDeliveryFoundException {
        log.info("Need to set order {} as successful", orderId);
        deliveryService.deliverySuccess(orderId);
    }

    @Override
    @PostMapping("/picked/{orderId}")
    public void deliveryPicked(@PathVariable UUID orderId) throws NoDeliveryFoundException {
        log.info("Need to set order {} as picked by delivery service", orderId);
        deliveryService.deliveryPicked(orderId);

    }

    @Override
    @PostMapping("/failed/{orderId}")
    public void deliveryFail(@PathVariable UUID orderId) throws NoDeliveryFoundException {
        log.info("Need to set order {} as failed", orderId);
        deliveryService.deliveryFail(orderId);

    }

    @Override
    @PostMapping("/cost")
    public Double deliveryCostCalc(@RequestBody OrderDto orderDto) throws NoDeliveryFoundException {
        log.info("Need to calculate delivery cost for order {}", orderDto);
        Double deliveryCost = deliveryService.deliveryCostCalc(orderDto);
        log.info("Delivery cost is {}, order: {}", deliveryCost, orderDto);
        return deliveryCost;
    }
}
