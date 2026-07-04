package ru.yandex.practicum.commerce.dto.shopping.product;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SortObject {
    @NotNull
    private String direction; //"ASC" or "DESC"
    @NotNull
    private String property;

}
