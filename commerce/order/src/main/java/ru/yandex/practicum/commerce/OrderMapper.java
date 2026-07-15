package ru.yandex.practicum.commerce;

import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.dto.shopping.order.OrderDto;
import ru.yandex.practicum.commerce.dto.warehouse.warehouse.AssemblyProductsForOrderRequest;

@Mapper(componentModel = "spring", uses=MapFunctions.class)
public interface OrderMapper {

    OrderDto toDto(Order order);

    AssemblyProductsForOrderRequest toAssemblyDto(Order order);
}
