package ru.yandex.practicum.commerce;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.commerce.dto.warehouse.product.NewProductInWarehouseRequest;

@Mapper(componentModel = "spring")
public interface WarehouseProductMapper {

    @Mapping(target = "width", source = "productNewRequest.dimension.width")
    @Mapping(target = "height", source = "productNewRequest.dimension.height")
    @Mapping(target = "depth", source = "productNewRequest.dimension.depth")
    @Mapping(target = "quantity", constant = "0L")
    WarehouseProduct fromNewRequest(NewProductInWarehouseRequest productNewRequest);
}
