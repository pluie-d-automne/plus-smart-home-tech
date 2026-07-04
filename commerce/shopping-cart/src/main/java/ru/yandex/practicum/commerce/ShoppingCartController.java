package ru.yandex.practicum.commerce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.shopping.cart.ShoppingCartOperations;
import ru.yandex.practicum.commerce.dto.shopping.cart.ChangeProductQuantityRequestDto;
import ru.yandex.practicum.commerce.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.exception.ShoppingCartNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartOperations {
    private final ShoppingCartService cartService;

    @Override
    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam String username) throws NotAuthorizedUserException {
        log.info("Getting shopping cart info for user {}", username);
        ShoppingCartDto shoppingCartDto = cartService.getShoppingCart(username);
        log.info("Shopping cart for user {}: {}", username,  shoppingCartDto);
        return shoppingCartDto;
    }

    @Override
    @PutMapping
    public ShoppingCartDto addProductsToCart(@RequestParam String username,
                                      @RequestBody Map<UUID, Long> productsToAdd) throws NotAuthorizedUserException {
        log.info("Adding products: {} to the shopping cart of user {}", productsToAdd, username);
        ShoppingCartDto shoppingCartDto = cartService.addProductsToCart(username, productsToAdd);
        log.info("Shopping cart for user {}: {}", username,  shoppingCartDto);
        return shoppingCartDto;
    }

    @Override
    @DeleteMapping
    public void deactivateCart(@RequestParam String username) throws NotAuthorizedUserException, ShoppingCartNotFoundException {
        log.info("Deactivating shopping cart for user {}", username);
        cartService.deactivateCart(username);
    }

    @Override
    @PostMapping("/remove")
    public ShoppingCartDto removeProductsFromCart(@RequestParam String username,
                                           @RequestBody List<UUID> productIds) throws NotAuthorizedUserException, NoProductsInShoppingCartException {
        log.info("User {} wants to remove products from cart: {}", username, productIds);
        ShoppingCartDto shoppingCart = cartService.removeProductsFromCart(username, productIds);
        log.info("Shopping cart was updated: {}", shoppingCart);
        return shoppingCart;
    }

    @Override
    @PostMapping("/change-quantity")
    public ShoppingCartDto updateQuantitiesInCart(@RequestParam String username,
                                           @RequestBody ChangeProductQuantityRequestDto productToChange) throws NotAuthorizedUserException, NoProductsInShoppingCartException {
        log.info("User {} wants to update product {} in his cart: ", username, productToChange);
        ShoppingCartDto shoppingCart = cartService.updateQuantitiesInCart(username, productToChange);
        log.info("Shopping cart was updated: {}", shoppingCart);
        return shoppingCart;
    }
}
