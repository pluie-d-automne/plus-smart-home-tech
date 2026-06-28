package ru.yandex.practicum.commerce.dto.warehouse.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewProductInWarehouseRequest {
    UUID productId;
    Boolean fragile;
    DimensionDto dimension;
    Double weight;
}
