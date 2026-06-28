package ru.yandex.practicum.commerce;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class WarehouseProduct {
    @Id
    @Column(name = "uuid", nullable = false)
    private UUID productId;

    @Column(name = "fragile", nullable = false)
    private Boolean fragile;

    @Column(name = "width", nullable = true)
    private Double width;

    @Column(name = "height", nullable = true)
    private Double height;

    @Column(name = "depth", nullable = true)
    private Double depth;

    @Column(name = "weight", nullable = true)
    private Double weight;

    @Column(name = "quantity", nullable = false)
    @ColumnDefault("0")
    private Long quantity;
}