package ru.yandex.practicum.commerce;

import ru.yandex.practicum.commerce.dto.shopping.product.PageProductDto;
import ru.yandex.practicum.commerce.dto.shopping.product.ProductDto;
import ru.yandex.practicum.commerce.dto.shopping.product.SetProductQuantityStateRequest;
import ru.yandex.practicum.commerce.exception.ProductNotFoundException;

import java.util.List;

public interface ProductService {
    ProductDto create(ProductDto productDto);

    ProductDto getProductById(String productId) throws ProductNotFoundException;

    ProductDto update(ProductDto productDto) throws ProductNotFoundException;

    boolean delete(String productId) throws ProductNotFoundException;

    boolean updateQuantityState(SetProductQuantityStateRequest quantityState) throws ProductNotFoundException;

    PageProductDto getProductsByCategory(String category, int page, int size, List<String> sort);
}
