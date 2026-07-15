package ru.yandex.practicum.commerce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.shopping.order.OrderOperations;
import ru.yandex.practicum.commerce.dto.shopping.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.dto.shopping.order.OrderDto;
import ru.yandex.practicum.commerce.dto.shopping.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.NotAuthorizedUserException;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/order")
@RequiredArgsConstructor
public class OrderController implements OrderOperations {
    private final OrderService orderService;

    @Override
    @GetMapping
    public List<OrderDto> getOrdersByUser(@RequestParam String username) throws NotAuthorizedUserException {
        log.info("Need to get orders of user {}", username);
        List<OrderDto> ordersDto = orderService.getOrdersByUser(username);
        log.info("User {} has the following orders: {}", username, ordersDto);
        return ordersDto;
    }

    @Override
    @PutMapping
    public OrderDto createNewOrder(@RequestParam String username,
                            @RequestBody CreateNewOrderRequest request) throws NoSpecifiedProductInWarehouseException {
        log.info("Need to create new order {} for user {}", request, username);
        OrderDto orderDto = orderService.createNewOrder(username, request);
        log.info("Order created: {}", orderDto);
        return orderDto;
    }

    @Override
    @PostMapping("/return")
    public OrderDto returnProducts(@RequestBody ProductReturnRequest request) throws NoOrderFoundException {
        log.info("Need to return products: {}", request);
        OrderDto orderDto = orderService.returnProducts(request);
        log.info("Order after products return: {}", orderDto);
        return orderDto;
    }

    @Override
    @PostMapping("/payment/{orderId}")
    public OrderDto pay(@PathVariable UUID orderId) throws NoOrderFoundException {
        log.info("Need to pay for order {}", orderId);
        OrderDto orderDto = orderService.pay(orderId);
        log.info("Payed order: {}", orderDto);
        return orderDto;
    }

    @Override
    @PostMapping("/payment/failed/{orderId}")
    public OrderDto failPayment(@PathVariable UUID orderId) throws NoOrderFoundException {
        log.info("Need to fail payment for order {}", orderId);
        OrderDto orderDto = orderService.failPayment(orderId);
        log.info("Order with failed payment: {}", orderDto);
        return  orderDto;
    }

    @Override
    @PostMapping("/delivery/{orderId}")
    public OrderDto deliver(@PathVariable UUID orderId) throws NoOrderFoundException {
        log.info("Need to deliver order {}", orderId);
        OrderDto orderDto = orderService.deliver(orderId);
        log.info("Delivered order: {}", orderDto);
        return orderDto;
    }

    @Override
    @PostMapping("/delivery/failed/{orderId}")
    public OrderDto failDelivery(@PathVariable UUID orderId) throws NoOrderFoundException {
        log.info("Need to fail delivery for order {}", orderId);
        OrderDto orderDto = orderService.failDelivery(orderId);
        log.info("Order with failed delivery: {}", orderDto);
        return orderDto;
    }

    @Override
    @PostMapping("/completed/{orderId}")
    public OrderDto complete(@PathVariable UUID orderId) throws NoOrderFoundException {
        log.info("Need to complete order {}", orderId);
        OrderDto orderDto = orderService.complete(orderId);
        log.info("Completed order: {}", orderDto);
        return orderDto;
    }

    @Override
    @PostMapping("/calculate/total/{orderId}")
    public OrderDto calcTotal(@PathVariable UUID orderId) throws NoOrderFoundException {
        log.info("Need calculate total cost for order {}", orderId);
        OrderDto orderDto = orderService.calcTotal(orderId);
        log.info("Order with total cost: {}", orderDto);
        return orderDto;
    }

    @Override
    @PostMapping("/calculate/delivery/{orderId}")
    public OrderDto calcDelivery(@PathVariable UUID orderId) throws NoOrderFoundException {
        log.info("Need to calculate delivery price for order {}", orderId);
        OrderDto orderDto = orderService.calcDelivery(orderId);
        log.info("Order with delivery cost: {}", orderDto);
        return orderDto;

    }

    @Override
    @PostMapping("/assembly/{orderId}")
    public OrderDto assemble(@PathVariable UUID orderId) throws NoOrderFoundException {
        log.info("Need to asseble order {}, orderId");
        OrderDto orderDto = orderService.assemble(orderId);
        log.info("Assembled order: {}", orderDto);
        return orderDto;
    }

    @Override
    @PostMapping("/assembly/failed/{orderId}")
    public OrderDto failAssembly(@PathVariable UUID orderId) throws NoOrderFoundException {
        log.info("Need to fail assembly for order {}", orderId);
        OrderDto orderDto = orderService.failAssembly(orderId);
        log.info("Order with assembly failed: {}", orderDto);
        return orderDto;
    }
}
