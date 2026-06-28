package ru.yandex.practicum.commerce;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "products")
public class WarehouseProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", nullable = false)
    UUID productId;

    @Column(name = "fragile", nullable = false)
    Boolean fragile;

    @Column(name = "width", nullable = true)
    Double width;

    @Column(name = "height", nullable = true)
    Double height;

    @Column(name = "depth", nullable = true)
    Double depth;

    @Column(name = "weight", nullable = true)
    Double weight;

    @Column(name = "quantity", nullable = false)
    Long quantity;
}