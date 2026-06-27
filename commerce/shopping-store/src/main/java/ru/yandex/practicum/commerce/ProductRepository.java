package ru.yandex.practicum.commerce;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.commerce.dto.shopping.product.ProductCategory;

import java.util.UUID;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByProductCategory(ProductCategory category, Pageable pageable);
}
