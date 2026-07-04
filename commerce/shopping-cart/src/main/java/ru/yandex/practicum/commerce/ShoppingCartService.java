package ru.yandex.practicum.commerce;

import ru.yandex.practicum.commerce.dto.shopping.cart.ChangeProductQuantityRequestDto;
import ru.yandex.practicum.commerce.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.exception.ShoppingCartNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {
    ShoppingCartDto addProductsToCart(String username,
                                      Map<UUID, Long> productsToAdd) throws NotAuthorizedUserException;

    ShoppingCartDto getShoppingCart(String username) throws NotAuthorizedUserException;

    void deactivateCart(String username) throws NotAuthorizedUserException, ShoppingCartNotFoundException;

    ShoppingCartDto removeProductsFromCart(String username,
                                           List<UUID> productIds) throws NotAuthorizedUserException, NoProductsInShoppingCartException;

    ShoppingCartDto updateQuantitiesInCart(String username,
                                           ChangeProductQuantityRequestDto productToChange) throws NotAuthorizedUserException, NoProductsInShoppingCartException;
}
