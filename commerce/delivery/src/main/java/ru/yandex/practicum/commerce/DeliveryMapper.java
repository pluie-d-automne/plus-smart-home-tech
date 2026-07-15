package ru.yandex.practicum.commerce;

import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    DeliveryDto toDto(Delivery delivery);

    Delivery fromDto(DeliveryDto deliveryDto);
}
