package ru.yandex.practicum.commerce;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.shopping.store.ShoppingStoreOperations;
import ru.yandex.practicum.commerce.dto.shopping.product.PageProductDto;
import ru.yandex.practicum.commerce.dto.shopping.product.ProductDto;
import ru.yandex.practicum.commerce.dto.shopping.product.SetProductQuantityStateRequest;
import ru.yandex.practicum.commerce.exception.ProductNotFoundException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreOperations {
    private final ProductService productService;


    @Override
    @PutMapping
    public ProductDto addProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("Adding new product {}", productDto);
        ProductDto savedProductDto = productService.create(productDto);
        log.info("Saved new product: {}", savedProductDto);
        return savedProductDto;
    }


    @Override
    @GetMapping("/{productId}")
    public ProductDto getProductById(@PathVariable String productId) throws ProductNotFoundException {
        log.info("Looking for product by id={}", productId);
        ProductDto product = productService.getProductById(productId);
        log.info("Found product: {}", product);
        return product;
    }


    @Override
    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto productDto) throws ProductNotFoundException {
        log.info("Updating product {}", productDto);
        ProductDto product = productService.update(productDto);
        log.info("Updated product: {}", product);
        return product;
    }


    @Override
    @PostMapping("/removeProductFromStore")
    public boolean deleteProduct(@RequestBody String productId) throws ProductNotFoundException {
        log.info("Deleting product with id={}", productId);
        boolean result = productService.delete(productId);

        if (result) {
            log.info("Deleted product with id={}", productId);
        } else {
            log.info("Failed to delete product with id={}", productId);
        }

        return result;
    }

    @Override
    @PostMapping("/quantityState")
    public boolean updateQuantityState(@RequestBody SetProductQuantityStateRequest quantityState) throws ProductNotFoundException {
        log.info("Updating quantityState: {}", quantityState);
        boolean result = productService.updateQuantityState(quantityState);

        if (result) {
            log.info("Updated quantityState: {}", quantityState);
        } else {
            log.info("Failed to update quantityState: {}", quantityState);
        }

        return result;
    }

    @Override
    @GetMapping
    public PageProductDto getProductsByCategory(@RequestParam String category,
                                         @RequestParam int page,
                                         @RequestParam int size,
                                         @RequestParam List<String> sort) {
        log.info("Get products by category {}, page={}, size={}, sort={}", category, page, size, sort);
        PageProductDto result = productService.getProductsByCategory(category, page, size, sort);
        log.info("Result: {}", result);
        return result;
    }
}
