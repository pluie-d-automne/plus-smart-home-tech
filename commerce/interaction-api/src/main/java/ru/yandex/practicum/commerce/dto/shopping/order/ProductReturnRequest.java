package ru.yandex.practicum.commerce.dto.shopping.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class ProductReturnRequest {
    @NotNull
    @NotEmpty
    private UUID orderId;

    @NotNull
    @NotEmpty
    private Map<@NotNull UUID, @NotNull @Positive Long> products;
}
