package ru.yandex.practicum.commerce;

import ru.yandex.practicum.commerce.dto.shopping.product.PageProductDto;
import ru.yandex.practicum.commerce.dto.shopping.product.ProductCategory;
import ru.yandex.practicum.commerce.dto.shopping.product.ProductDto;
import ru.yandex.practicum.commerce.dto.shopping.product.SetProductQuantityStateRequest;
import ru.yandex.practicum.commerce.exception.ProductNotFoundException;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductDto create(ProductDto productDto);

    ProductDto getProductById(UUID productId) throws ProductNotFoundException;

    ProductDto update(ProductDto productDto) throws ProductNotFoundException;

    boolean delete(UUID productId) throws ProductNotFoundException;

    boolean updateQuantityState(SetProductQuantityStateRequest quantityState) throws ProductNotFoundException;

    PageProductDto getProductsByCategory(ProductCategory category, Integer page, Integer size, List<String> sort);
}
