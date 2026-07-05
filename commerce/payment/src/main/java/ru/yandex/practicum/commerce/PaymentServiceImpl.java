package ru.yandex.practicum.commerce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.contract.shopping.order.OrderOperations;
import ru.yandex.practicum.commerce.contract.shopping.store.ShoppingStoreOperations;
import ru.yandex.practicum.commerce.dto.shopping.order.OrderDto;
import ru.yandex.practicum.commerce.dto.shopping.order.PaymentDto;
import ru.yandex.practicum.commerce.dto.shopping.order.PaymentState;
import ru.yandex.practicum.commerce.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.exception.NotEnoughInfoInOrderToCalculateException;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final ShoppingStoreOperations shoppingStoreOperations;
    private final OrderOperations orderOperations;
    private final PaymentMapper paymentMapper;

    private final static Double FEE_RATE = 0.1;

    @Override
    public Double calcProductCost(OrderDto orderDto) throws NotEnoughInfoInOrderToCalculateException {
        Map<UUID, Long> products = orderDto.getProducts();
        Double productPrice = 0d;
        Double price;

        if (products.size()==0) {
            throw new NotEnoughInfoInOrderToCalculateException("There are no products in order: " + orderDto);
        }

        for (UUID productId : products.keySet()) {
            price = shoppingStoreOperations.getProductById(productId).getPrice();
            productPrice += price * products.get(productId);
        }

        return productPrice;
    }


    @Override
    public Double calcTotalCost(OrderDto orderDto) throws NotEnoughInfoInOrderToCalculateException {
        Double totalCost = 0d;

        if (orderDto.getDeliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("deliveryPrice should not be empty: " + orderDto);
        } else if (orderDto.getProductPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("productPrice should not be empty: " + orderDto);
        }

        totalCost += orderDto.getProductPrice() * (1 + FEE_RATE);
        totalCost += orderDto.getDeliveryPrice();
        return totalCost;
    }


    @Override
    public PaymentDto createPayment(OrderDto orderDto) throws NotEnoughInfoInOrderToCalculateException {
        if (orderDto.getDeliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("deliveryPrice should not be empty: " + orderDto);
        } else if (orderDto.getProductPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("productPrice should not be empty: " + orderDto);
        }

        Payment payment = paymentRepository.save(Payment.builder()
                        .orderId(orderDto.getOrderId())
                        .shoppingCartId(orderDto.getShoppingCartId())
                        .totalPayment(orderDto.getTotalPrice())
                        .deliveryTotal(orderDto.getDeliveryPrice())
                        .feeTotal(orderDto.getProductPrice() * FEE_RATE)
                        .productPrice(orderDto.getProductPrice())
                        .paymentState(PaymentState.PENDING)
                        .build());

        return paymentMapper.toDto(payment);
    }


    @Override
    public void refund(UUID paymentId) throws NoOrderFoundException {
        Payment payment  = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoOrderFoundException("No payment with id="+ paymentId));

        payment.setPaymentState(PaymentState.SUCCESS);
        orderOperations.pay(payment.getOrderId());
        paymentRepository.save(payment);
    }

    @Override
    public void failPayment(UUID paymentId) throws NoOrderFoundException {
        Payment payment  = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoOrderFoundException("No payment with id="+ paymentId));

        payment.setPaymentState(PaymentState.FAILED);
        orderOperations.pay(payment.getOrderId());
        paymentRepository.save(payment);
    }
}
