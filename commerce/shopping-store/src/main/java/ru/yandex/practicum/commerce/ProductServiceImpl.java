package ru.yandex.practicum.commerce;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.dto.shopping.product.PageProductDto;
import ru.yandex.practicum.commerce.dto.shopping.product.ProductCategory;
import ru.yandex.practicum.commerce.dto.shopping.product.ProductDto;
import ru.yandex.practicum.commerce.dto.shopping.product.QuantityState;
import ru.yandex.practicum.commerce.dto.shopping.product.SetProductQuantityStateRequest;
import ru.yandex.practicum.commerce.exception.ProductNotFoundException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    @Override
    public ProductDto create(ProductDto productDto) {
        Product product = productMapper.fromDto(productDto);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    @Override
    public ProductDto getProductById(UUID productId) throws ProductNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product with id=" + productId + " was not found"));
        return productMapper.toDto(product);
    }

    @Override
    public ProductDto update(ProductDto productDto) throws ProductNotFoundException {
        Product productNew = productMapper.fromDto(productDto);
        productRepository.findById(productNew.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product with id=" + productNew.getProductId() + " was not found"));
        Product product = productRepository.save(productNew);
        return productMapper.toDto(product);
    }

    @Override
    public boolean delete(UUID productId) throws ProductNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product with id=" + productId + " was not found"));
        try {
            productRepository.delete(product);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateQuantityState(SetProductQuantityStateRequest quantityState) throws ProductNotFoundException {
        UUID productId = quantityState.getProductId();
        QuantityState newQuantityStateValue = quantityState.getQuantityState();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product with id=" + productId + " was not found"));

        try {
            product.setQuantityState(newQuantityStateValue);
            productRepository.save(product);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public PageProductDto getProductsByCategory(ProductCategory category, int page, int size, List<String> sort) {
        Sort sorting = null;

        for (int x = 0; x < sort.size(); x = x+2) {
            if (sorting == null) {
                sorting = sort.get(x+1).equals("asc") ? Sort.by(sort.get(x)).ascending() : Sort.by(sort.get(x)).descending();
            } else {
                sorting = sort.get(x+1).equals("asc") ? sorting.and(Sort.by(sort.get(x)).ascending()) : sorting.and(Sort.by(sort.get(x)).descending());
            }
        }

        Pageable pageable = PageRequest.of(page, size, sorting);
        List<Product> products = productRepository.findByProductCategory(category, pageable);
        List<ProductDto> content = products.stream().map(x -> productMapper.toDto(x)).toList();
        return new PageProductDto(content);
    }
}
