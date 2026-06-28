package ru.yandex.practicum.commerce.dto.warehouse.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DimensionDto {
    Double width;
    Double height;
    Double depth;
}
