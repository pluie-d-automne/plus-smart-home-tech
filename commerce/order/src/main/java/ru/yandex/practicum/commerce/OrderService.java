package ru.yandex.practicum.commerce;

import ru.yandex.practicum.commerce.dto.shopping.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.dto.shopping.order.OrderDto;
import ru.yandex.practicum.commerce.dto.shopping.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.NotAuthorizedUserException;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    List<OrderDto> getOrdersByUser(String username) throws NotAuthorizedUserException;

    OrderDto createNewOrder(String username, CreateNewOrderRequest request) throws NoSpecifiedProductInWarehouseException;

    OrderDto returnProducts(ProductReturnRequest request) throws NoOrderFoundException;

    OrderDto pay(UUID orderId) throws NoOrderFoundException;

    OrderDto failPayment(UUID orderId) throws NoOrderFoundException;

    OrderDto deliver(UUID orderId) throws NoOrderFoundException;

    OrderDto failDelivery(UUID orderId) throws NoOrderFoundException;

    OrderDto complete(UUID orderId) throws NoOrderFoundException;

    OrderDto calcTotal(UUID orderId) throws NoOrderFoundException;

    OrderDto calcDelivery(UUID orderId) throws NoOrderFoundException;

    OrderDto assemble(UUID orderId) throws NoOrderFoundException;

    OrderDto failAssembly(UUID orderId) throws NoOrderFoundException;
}
