package ru.yandex.practicum.commerce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.contract.delivery.DeliveryOperations;
import ru.yandex.practicum.commerce.contract.shopping.payment.PaymentOperations;
import ru.yandex.practicum.commerce.contract.warehouse.WarehouseOperations;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryState;
import ru.yandex.practicum.commerce.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.shopping.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.dto.shopping.order.OrderDto;
import ru.yandex.practicum.commerce.dto.shopping.order.OrderState;
import ru.yandex.practicum.commerce.dto.shopping.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.dto.warehouse.booking.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.warehouse.AddressDto;
import ru.yandex.practicum.commerce.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.NotAuthorizedUserException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final MapFunctions func;
    private final DeliveryOperations deliveryOperations;
    private final WarehouseOperations warehouseOperations;
    private final PaymentOperations paymentOperations;

    @Override
    public List<OrderDto> getOrdersByUser(String username) throws NotAuthorizedUserException {
        if (username.isBlank()) {
            throw new NotAuthorizedUserException("Username should not be blank");
        }

        List<Order> orders = orderRepository.findByUser(username);
        return orders.stream().map(order -> orderMapper.toDto(order)).toList();
    }


    @Override
    public OrderDto createNewOrder(String username, CreateNewOrderRequest request) throws NoSpecifiedProductInWarehouseException {
        ShoppingCartDto shoppingCartDto = request.getShoppingCart();
        AddressDto addressToDto = request.getDeliveryAddress();
        AddressDto addressFromDto = warehouseOperations.getAddress();


        Order newOrder = orderRepository.save(Order.builder()
                        .user(username)
                        .shoppingCartId(shoppingCartDto.getShoppingCartId())
                        .state(OrderState.NEW)
                        .build()) ;
        log.info("Create new empty order: {}", newOrder);

        DeliveryDto deliveryDto = deliveryOperations.createNewDelivery(DeliveryDto.builder()
                        .orderId(newOrder.getOrderId())
                        .deliveryState(DeliveryState.CREATED)
                        .fromAddress(addressFromDto)
                        .toAddress(addressToDto)
                        .build());
        log.info("Create new delivery: {}", deliveryDto);
        newOrder.setDeliveryId(deliveryDto.getDeliveryId());

        Set<OrderContent> products = func.mapContent(shoppingCartDto.getProducts(), newOrder.getOrderId());
        log.info("Mapped  products {} from mapping {}", products, shoppingCartDto.getProducts());
        newOrder.setProducts(products);

        Order finalOrder = orderRepository.save(newOrder);
        log.info("Order was successfully created: {}", finalOrder);

        return orderMapper.toDto(finalOrder);
    }


    @Override
    public OrderDto returnProducts(ProductReturnRequest request) throws NoOrderFoundException {
        Order orderInitial = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NoOrderFoundException("Order with id=" + request.getOrderId() + " was not found"));
        log.info("Initial order products: {}", orderInitial.getProducts());

        for (UUID productId : request.getProducts().keySet()) {
            Long quantityToReturn = request.getProducts().get(productId);

            Long quantityInitial = orderInitial.getProducts().stream()
                    .filter(content -> content.getProductId().equals(productId))
                    .findFirst().get().getQuantity();

            Long quantityNew = quantityInitial <= quantityToReturn? 0 : quantityInitial - quantityToReturn;

            orderInitial.getProducts().stream()
                    .filter(content -> content.getProductId().equals(productId))
                    .findFirst().get().setQuantity(quantityNew);

        }

        log.info("Updated order products: {}", orderInitial.getProducts());

        Order finalOrder = orderRepository.save(orderInitial);
        log.info("Order was successfully updated: {}", finalOrder);
        warehouseOperations.returnProductsToWarehouse(request.getProducts());
        return orderMapper.toDto(finalOrder);

    }


    @Override
    public OrderDto pay(UUID orderId) throws NoOrderFoundException {
        Order orderInitial = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order with id=" + orderId + " was not found"));
        paymentOperations.refund(orderInitial.getPaymentId());
        orderInitial.setState(OrderState.PAID);
        Order finalOrder = orderRepository.save(orderInitial);
        log.info("Order was successfully payed: {}", finalOrder);
        return orderMapper.toDto(finalOrder);
    }


    @Override
    public OrderDto failPayment(UUID orderId) throws NoOrderFoundException {
        Order orderInitial = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order with id=" + orderId + " was not found"));
        paymentOperations.failPayment(orderInitial.getPaymentId());
        orderInitial.setState(OrderState.PAYMENT_FAILED);
        Order finalOrder = orderRepository.save(orderInitial);
        log.info("Payment failed for order: {}", finalOrder);
        return orderMapper.toDto(finalOrder);

    }


    @Override
    public OrderDto deliver(UUID orderId) throws NoOrderFoundException {
        Order orderInitial = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order with id=" + orderId + " was not found"));
        deliveryOperations.deliverySuccess(orderInitial.getDeliveryId());
        orderInitial.setState(OrderState.DELIVERED);
        Order finalOrder = orderRepository.save(orderInitial);
        log.info("Order delivered: {}", finalOrder);
        return orderMapper.toDto(finalOrder);
    }


    @Override
    public OrderDto failDelivery(UUID orderId) throws NoOrderFoundException {
        Order orderInitial = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order with id=" + orderId + " was not found"));
        deliveryOperations.deliveryFail(orderInitial.getDeliveryId());
        orderInitial.setState(OrderState.DELIVERY_FAILED);
        Order finalOrder = orderRepository.save(orderInitial);
        log.info("Order delivery failed: {}", finalOrder);
        return orderMapper.toDto(finalOrder);
    }


    @Override
    public OrderDto complete(UUID orderId) throws NoOrderFoundException {
        Order orderInitial = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order with id=" + orderId + " was not found"));
        orderInitial.setState(OrderState.COMPLETED);
        Order finalOrder = orderRepository.save(orderInitial);
        log.info("Order completed: {}", finalOrder);
        return orderMapper.toDto(finalOrder);
    }


    @Override
    public OrderDto calcTotal(UUID orderId) throws NoOrderFoundException {
        Order orderInitial = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order with id=" + orderId + " was not found"));

        Double productPrice = paymentOperations.calcProductCost(orderMapper.toDto(orderInitial));
        log.info("Product price calculated: {}", productPrice);
        orderInitial.setProductPrice(productPrice);

        Double totalPrice = paymentOperations.calcTotalCost(orderMapper.toDto(orderInitial));
        log.info("Total price calculated: {}", totalPrice);
        orderInitial.setTotalPrice(totalPrice);

        UUID paymentId = paymentOperations.createPayment(orderMapper.toDto(orderInitial)).getPaymentId();
        log.info("New payment created: {}", paymentId);
        orderInitial.setPaymentId(paymentId);

        Order finalOrder = orderRepository.save(orderInitial);
        log.info("Order was successfully updated: {}", finalOrder);
        return orderMapper.toDto(finalOrder);
    }


    @Override
    public OrderDto calcDelivery(UUID orderId) throws NoOrderFoundException {
        Order orderInitial = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order with id=" + orderId + " was not found"));

        Double deliveryCost = deliveryOperations.deliveryCostCalc(orderMapper.toDto(orderInitial));
        log.info("Delivery cost calculated: {}", deliveryCost);
        orderInitial.setDeliveryPrice(deliveryCost);

        Order finalOrder = orderRepository.save(orderInitial);
        log.info("Order was successfully updated: {}", finalOrder);
        return orderMapper.toDto(finalOrder);
    }


    @Override
    public OrderDto assemble(UUID orderId) throws NoOrderFoundException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order with id=" + orderId + " was not found"));

        BookedProductsDto bookedProductsDto = warehouseOperations.assembly(orderMapper.toAssemblyDto(order));
        log.info("Booked products data: {}", bookedProductsDto);

        order.setFragile(bookedProductsDto.getFragile());
        order.setDeliveryWeight(bookedProductsDto.getDeliveryWeight());
        order.setDeliveryVolume(bookedProductsDto.getDeliveryVolume());

        Order finalOrder = orderRepository.save(order);
        log.info("Products for order were booked: {}", finalOrder);
        return orderMapper.toDto(finalOrder);
    }


    @Override
    public OrderDto failAssembly(UUID orderId) throws NoOrderFoundException {
        Order orderInitial = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order with id=" + orderId + " was not found"));
        orderInitial.setState(OrderState.ASSEMBLY_FAILED);
        Order finalOrder = orderRepository.save(orderInitial);
        log.info("Order assembly failed: {}", finalOrder);
        return orderMapper.toDto(finalOrder);
    }
}
