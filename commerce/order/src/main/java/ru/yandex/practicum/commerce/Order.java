package ru.yandex.practicum.commerce;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.commerce.dto.shopping.order.OrderState;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", nullable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private OrderState state;

    @Column(name = "user_name", nullable = false)
    private String user;

    @Column(name = "shopping_cart_id", nullable = true)
    private UUID shoppingCartId;

    @Column(name = "payment_id", nullable = true)
    private UUID paymentId;

    @Column(name = "delivery_id", nullable = true)
    private UUID deliveryId;

    @Column(name = "fragile", nullable = true)
    private Boolean fragile;

    @Column(name = "delivery_weight", nullable = true)
    private Double deliveryWeight;

    @Column(name = "delivery_volume", nullable = true)
    private Double deliveryVolume;

    @Column(name = "total_price", nullable = true)
    private Double totalPrice;

    @Column(name = "delivery_price", nullable = true)
    private Double deliveryPrice;

    @Column(name = "product_price", nullable = true)
    private Double productPrice;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="order_id", referencedColumnName="uuid")
    private Set<OrderContent> products;
}
