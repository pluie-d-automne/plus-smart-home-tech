package ru.yandex.practicum.commerce;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", nullable = false)
    private UUID paymentId;

    @Column(name = "order_id", nullable = true)
    private UUID orderId;

    @Column(name = "shopping_cart_id", nullable = true)
    private UUID shoppingCartId;

    @Column(name = "total_payment", nullable = true)
    private Double totalPayment;

    @Column(name = "delivery_total", nullable = true)
    private Double deliveryTotal;

    @Column(name = "fee_total", nullable = true)
    private Double feeTotal;
}
