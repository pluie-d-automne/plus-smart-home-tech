package ru.yandex.practicum.commerce;

import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.dto.shopping.cart.ShoppingCartDto;

@Mapper(componentModel = "spring", uses=MapFunctions.class)
public interface ShoppingCartMapper {

    ShoppingCartDto toDto(ShoppingCart cart);

}
