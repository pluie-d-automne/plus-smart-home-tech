package ru.yandex.practicum.commerce;

import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.dto.shopping.product.ProductDto;
import ru.yandex.practicum.commerce.dto.warehouse.product.NewProductInWarehouseRequest;

@Mapper(componentModel = "spring")
public interface WarehouseProductMapper {

    WarehouseProduct fromNewRequest(NewProductInWarehouseRequest productNewRequest);
}
