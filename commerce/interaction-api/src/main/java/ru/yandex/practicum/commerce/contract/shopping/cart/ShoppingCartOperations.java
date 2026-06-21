package ru.yandex.practicum.commerce.contract.shopping.cart;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.commerce.dto.shopping.cart.ShoppingCartDto;

// От этих интерфейсов должен наследоваться контроллер
@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
//@Tag(
//        name = "Корзина для онлайн-магазина",
//        description = "API для обеспечения работы корзины онлайн-магазина"
//)
public interface ShoppingCartOperations {
//    @Operation(description = "Получить актуальную корзину для авторизованного пользователя.")
//    @ApiResponses(
//            value = {
//                    @ApiResponse(
//                            responseCode = "200",
//                            description =
//                                    "Ранее созданная или новая корзина в онлайн-магазине",
//                            content = @Content(schema = @Schema(implementation = ShoppingCartDto.class), mediaType =)
//                    )
//                    @ApiResponse(
//                            responseCode = "401",
//                            description = "Имя пользователя не должно быть пустым.",
//                            content = @Content(schema = @Schema(implementation = NotAuthorizedUserException.class))
//                    )
//            })
    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam String username) throws NotAuthorizedUserException;
}
