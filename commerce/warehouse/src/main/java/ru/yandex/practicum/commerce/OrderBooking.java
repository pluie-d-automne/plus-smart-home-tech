package ru.yandex.practicum.commerce;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_bookings")
public class OrderBooking {
    @Id
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "delivery_id", nullable = true)
    private UUID deliveryId;

    @ElementCollection
    @CollectionTable(name="booked_products",
            joinColumns=@JoinColumn(name="order_id"))
    @MapKeyColumn(name="product_id")
    @Column(name="quantity")
    private Map<UUID, Long> products;
}
