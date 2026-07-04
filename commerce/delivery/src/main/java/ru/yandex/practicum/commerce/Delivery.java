package ru.yandex.practicum.commerce;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryState;

import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "deliveries")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", nullable = false)
    private UUID deliveryId;

    @Column(name = "order_id", nullable = true)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private DeliveryState deliveryState;

    @Column(name = "delivery_weight", nullable = false)
    private Double DeliveryWeight;

    @Column(name = "delivery_volume", nullable = false)
    private Double DeliveryVolume;

    @Column(name = "fragile", nullable = false)
    private Boolean Fragile;

    @Column(name = "delivery_cost", nullable = false)
    private Double deliveryCost;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_address_uuid", nullable = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private Address fromAddress;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_address_uuid", nullable = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private Address toAddress;
}
