package ru.yandex.practicum.commerce;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shopping_cart_content")
public class ShoppingCartContent {
    @Id
    @Column(name = "product_id", nullable = false)
    UUID productId;

    @Column(name = "quantity", nullable = false)
    Long quantity;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_cart_id", nullable = false)
    ShoppingCart cart;
}
