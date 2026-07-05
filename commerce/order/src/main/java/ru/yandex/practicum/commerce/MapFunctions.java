package ru.yandex.practicum.commerce;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class MapFunctions {
    public Set<OrderContent> mapContent(Map<UUID,Long> productQuantities, UUID orderId) {
        Set<OrderContent> content = new HashSet<>();

        for (UUID uuid : productQuantities.keySet()) {
            content.add(OrderContent.builder()
                    .productId(uuid)
                    .quantity(productQuantities.get(uuid))
                    .orderId(orderId)
                    .build());
        }

        return content;
    }

    public Map<UUID,Long> mapContentToDto(Set<OrderContent> content) {
        Map<UUID,Long> products = new HashMap<>();

        if (content!=null) {
            for (OrderContent product : content) {
                products.put(product.getProductId(), product.getQuantity());
            }
        }

        return products;
    }
}
