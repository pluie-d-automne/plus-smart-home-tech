package ru.yandex.practicum.commerce.contract.shopping.cart;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.commerce.dto.shopping.cart.ChangeProductQuantityRequestDto;
import ru.yandex.practicum.commerce.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.exception.ShoppingCartNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartOperations {
    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam String username) throws NotAuthorizedUserException;

    @PutMapping
    ShoppingCartDto addProductsToCart(@RequestParam String username,
                                      @RequestBody Map<UUID, Long> productsToAdd) throws NotAuthorizedUserException;

    @DeleteMapping
    void deactivateCart(@RequestParam String username) throws NotAuthorizedUserException, ShoppingCartNotFoundException;

    @PostMapping("/remove")
    ShoppingCartDto removeProductsFromCart(@RequestParam String username,
                                      @RequestBody List<UUID> productIds) throws NotAuthorizedUserException, NoProductsInShoppingCartException;

    @PostMapping("/change-quantity")
    ShoppingCartDto updateQuantitiesInCart(@RequestParam String username,
                                           @RequestBody ChangeProductQuantityRequestDto productToChange) throws NotAuthorizedUserException, NoProductsInShoppingCartException;
}
