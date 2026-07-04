package ru.yandex.practicum.commerce.contract.shopping.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.commerce.dto.shopping.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.dto.shopping.order.OrderDto;
import ru.yandex.practicum.commerce.dto.shopping.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.NotAuthorizedUserException;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderOperations {
    @GetMapping
    List<OrderDto> getOrdersByUser(@RequestParam String username) throws NotAuthorizedUserException;

    @PutMapping
    OrderDto createNewOrder(@RequestBody CreateNewOrderRequest request) throws NoSpecifiedProductInWarehouseException;

    @PostMapping("/return")
    OrderDto returnProducts(@RequestBody ProductReturnRequest request) throws NoOrderFoundException;

    @PostMapping("/payment")
    OrderDto pay(@PathVariable UUID orderId) throws NoOrderFoundException;

    @PostMapping("/payment/failed")
    OrderDto failPayment(@PathVariable UUID orderId) throws NoOrderFoundException;

    @PostMapping("/delivery")
    OrderDto deliver(@PathVariable UUID orderId) throws NoOrderFoundException;

    @PostMapping("/delivery/failed")
    OrderDto failDelivery(@PathVariable UUID orderId) throws NoOrderFoundException;

    @PostMapping("/completed")
    OrderDto complete(@PathVariable UUID orderId) throws NoOrderFoundException;

    @PostMapping("/calculate/total")
    OrderDto calcTotal(@PathVariable UUID orderId) throws NoOrderFoundException;

    @PostMapping("/calculate/delivery")
    OrderDto calcDelivery(@PathVariable UUID orderId) throws NoOrderFoundException;

    @PostMapping("/assembly")
    OrderDto assemble(@PathVariable UUID orderId) throws NoOrderFoundException;

    @PostMapping("/assembly/failed")
    OrderDto failAssembly(@PathVariable UUID orderId) throws NoOrderFoundException;
}
