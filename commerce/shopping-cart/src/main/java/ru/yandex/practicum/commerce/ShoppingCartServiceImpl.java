package ru.yandex.practicum.commerce;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.dto.shopping.cart.ChangeProductQuantityRequestDto;
import ru.yandex.practicum.commerce.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.exception.ShoppingCartNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository cartRepository;
    private final ShoppingCartMapper cartMapper;
    private final MapFunctions func;

    @Override
    @Transactional
    public ShoppingCartDto addProductsToCart(String username,
                                      Map<UUID, Long> productsToAdd) throws NotAuthorizedUserException {
        Set<ShoppingCartContent> products;

        if (username.isBlank()) {
            throw new NotAuthorizedUserException("Username should not be blank");
        }

        ShoppingCart shoppingCart;
        Optional<ShoppingCart> shoppingCartCheck = cartRepository.findByUserAndState(username, ShoppingCartState.ACTIVE);

        if (shoppingCartCheck.isPresent()) {
            shoppingCart = shoppingCartCheck.get();
            log.info("User {} already has an active shopping cart: {}", username, shoppingCart);
        } else {
            shoppingCart = ShoppingCart.builder().user(username).state(ShoppingCartState.ACTIVE).build();
            log.info("Creating new shopping cart: {}", shoppingCart);
        }

        Set<ShoppingCartContent> newProducts = func.mapContent(productsToAdd);
        log.info("Mapped  newProducts {} from mapping {}", newProducts, productsToAdd);
        if (shoppingCart.getProducts() != null) {
            products = shoppingCart.getProducts();
            products.addAll(newProducts);
        } else {
            products = newProducts;
        }

        log.info("Updated products: {}", products);
        shoppingCart.setProducts(products);
        log.info("Shopping cart to be saved: {}", shoppingCart);
        ShoppingCart newShoppingCart = cartRepository.save(shoppingCart);
        log.info("Shopping cart was updated: {}", shoppingCart);
        return cartMapper.toDto(newShoppingCart);
    }

    @Override
    public ShoppingCartDto getShoppingCart(String username) throws NotAuthorizedUserException {

        if (username.isBlank()) {
            throw new NotAuthorizedUserException("Username should not be blank");
        }

        ShoppingCart shoppingCart;
        Optional<ShoppingCart> shoppingCartFound = cartRepository.findByUserAndState(username, ShoppingCartState.ACTIVE);

        if (shoppingCartFound.isPresent()) {
            shoppingCart = shoppingCartFound.get();
        } else {
            shoppingCart = cartRepository.save(ShoppingCart.builder()
                    .user(username)
                    .state(ShoppingCartState.ACTIVE)
                    .build());
        }
        log.info("Shopping cart found: {}", shoppingCart);
        return cartMapper.toDto(shoppingCart);
    }

    @Override
    @Transactional
    public void deactivateCart(String username) throws NotAuthorizedUserException, ShoppingCartNotFoundException {
        if (username.isBlank()) {
            throw new NotAuthorizedUserException("Username should not be blank");
        }

        ShoppingCart shoppingCart = cartRepository.findByUserAndState(username, ShoppingCartState.ACTIVE)
                .orElseThrow(() -> new ShoppingCartNotFoundException(
                        "Active shopping cart for user " + username + " was not found"));

        shoppingCart.setState(ShoppingCartState.DEACTIVATED);

        ShoppingCart updShoppingCart = cartRepository.save(shoppingCart);
        log.info("Shopping cart was deactivated: {}", updShoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto removeProductsFromCart(String username,
                                           List<UUID> productIds) throws NotAuthorizedUserException, NoProductsInShoppingCartException {
        if (username.isBlank()) {
            throw new NotAuthorizedUserException("Username should not be blank");
        }

        ShoppingCart shoppingCart;
        Optional<ShoppingCart> shoppingCartFound = cartRepository.findByUserAndState(username, ShoppingCartState.ACTIVE);

        if (shoppingCartFound.isPresent()) {
            shoppingCart = shoppingCartFound.get();
            Set<ShoppingCartContent> newProducts = shoppingCart.getProducts().stream()
                    .filter(product -> !productIds.contains(product.getProductId()))
                    .collect(Collectors.toSet());
            shoppingCart.setProducts(newProducts);
        } else {
            shoppingCart = cartRepository.save(ShoppingCart.builder()
                    .user(username)
                    .state(ShoppingCartState.ACTIVE)
                    .build());
        }
        ShoppingCart shoppingCartUpd = cartRepository.save(shoppingCart);
        return cartMapper.toDto(shoppingCartUpd);
    }

    @Override
    @Transactional
    public ShoppingCartDto updateQuantitiesInCart(String username,
                                           ChangeProductQuantityRequestDto productToChange) throws NotAuthorizedUserException, NoProductsInShoppingCartException {

        if (username.isBlank()) {
            throw new NotAuthorizedUserException("Username should not be blank");
        }

        UUID productId = productToChange.getProductId();
        Long newQauntity = productToChange.getNewQuantity();
        ShoppingCart shoppingCart;
        Set<ShoppingCartContent> newProducts = new HashSet<>();;
        Optional<ShoppingCart> shoppingCartFound = cartRepository.findByUserAndState(username, ShoppingCartState.ACTIVE);

        if (shoppingCartFound.isPresent()) {
            shoppingCart = shoppingCartFound.get();

            for (ShoppingCartContent product: shoppingCart.getProducts()) {
                if (product.getProductId().equals(productId)) {
                    product.setQuantity(newQauntity);
                }
                newProducts.add(product);
            }

            shoppingCart.setProducts(newProducts);
        } else {
            newProducts.add(ShoppingCartContent.builder().productId(productId).quantity(newQauntity).build());
            shoppingCart = cartRepository.save(ShoppingCart.builder()
                    .user(username)
                    .state(ShoppingCartState.ACTIVE)
                    .products(newProducts)
                    .build());
        }
        ShoppingCart shoppingCartUpd = cartRepository.save(shoppingCart);
        return cartMapper.toDto(shoppingCartUpd);
    }

}
