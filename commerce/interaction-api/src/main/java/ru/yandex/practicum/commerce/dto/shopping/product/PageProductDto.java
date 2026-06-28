package ru.yandex.practicum.commerce.dto.shopping.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageProductDto {
    private List<ProductDto> content;
    private List<SortObject> sort;
}
