package ru.yandex.practicum.commerce;

import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.dto.shopping.order.PaymentDto;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentDto toDto(Payment payment);

    Payment fromDto(PaymentDto paymentDto);
}
