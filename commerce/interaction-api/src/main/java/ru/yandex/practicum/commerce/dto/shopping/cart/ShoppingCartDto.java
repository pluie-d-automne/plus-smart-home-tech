package ru.yandex.practicum.commerce.dto.shopping.cart;

import java.util.UUID;

@Value
@Schema(description = "Корзина товаров в онлайн магазине.")
public class ShoppingCartDto {
    @Schema(
            description = "Идентификатор корзины в БД",
            example = "53aa35...",
            implementation = UUID.class)
    @NotNull UUID shoppingCartId;

    @Schema(description = "Отображение идентификатора товара на отобранное количество")
    @NotNull
    @NotEmpty
    Map<@NotNull UUID, @NotNull @Positive Long> products;
}
