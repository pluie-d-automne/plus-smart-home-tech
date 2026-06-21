package ru.yandex.practicum.commerce.contract.shopping.store;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.commerce.dto.shopping.product.PageProductDto;
import ru.yandex.practicum.commerce.dto.shopping.product.ProductDto;
import ru.yandex.practicum.commerce.dto.shopping.product.SetProductQuantityStateRequest;
import ru.yandex.practicum.commerce.exception.ProductNotFoundException;

import java.util.List;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-store")
public interface ShoppingStoreOperations {
    @PutMapping
    ProductDto addProduct(@RequestBody ProductDto productDto);

    @GetMapping
    PageProductDto getProductsByCategory(@RequestParam String category,
                                         @RequestParam int page,
                                         @RequestParam int size,
                                         @RequestParam List<String> sort);

    @GetMapping("/{productId}")
    ProductDto getProductById(@PathVariable String productId) throws ProductNotFoundException;

    @PostMapping
    ProductDto updateProduct(@RequestBody ProductDto productDto) throws ProductNotFoundException;

    @PostMapping("/removeProductFromStore")
    boolean deleteProduct(@RequestBody String productId) throws ProductNotFoundException;

    @PostMapping("/quantityState")
    boolean updateQuantityState(@RequestBody SetProductQuantityStateRequest quantityState) throws ProductNotFoundException;
}
