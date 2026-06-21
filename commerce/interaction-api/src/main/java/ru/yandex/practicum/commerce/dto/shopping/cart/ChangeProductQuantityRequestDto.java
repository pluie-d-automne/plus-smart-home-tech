package ru.yandex.practicum.commerce.dto.shopping.cart;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ChangeProductQuantityRequestDto {
    @NotNull
    UUID productId;

    @NotNull
    @PositiveOrZero
    int newQuantity;
}
