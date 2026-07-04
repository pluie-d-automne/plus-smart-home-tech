package ru.yandex.practicum.commerce;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class MapFunctions {
    public Set<ShoppingCartContent> mapContent(Map<UUID,Long> productQuantities, UUID shoppingCartId) {
        Set<ShoppingCartContent> content = new HashSet<>();

        for (UUID uuid : productQuantities.keySet()) {
            content.add(ShoppingCartContent.builder()
                    .productId(uuid)
                    .quantity(productQuantities.get(uuid))
                    .shoppingCartId(shoppingCartId)
                    .build());
        }

        return content;
    }

    public Map<UUID,Long> mapContentToDto(Set<ShoppingCartContent> content) {
        Map<UUID,Long> products = new HashMap<>();

        if (content!=null) {
            for (ShoppingCartContent product : content) {
                products.put(product.getProductId(), product.getQuantity());
            }
        }

        return products;
    }
}
