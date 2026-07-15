package ru.yandex.practicum.commerce;

import ru.yandex.practicum.commerce.dto.warehouse.booking.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.product.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.product.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.warehouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warehouse.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.commerce.dto.warehouse.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.exception.SpecifiedProductAlreadyInWarehouseException;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    void addProduct(NewProductInWarehouseRequest productRequest) throws SpecifiedProductAlreadyInWarehouseException;

    BookedProductsDto checkBookingProducts(ShoppingCartDto shoppingCartDto) throws ProductInShoppingCartLowQuantityInWarehouse;

    void addProductQuantity(AddProductToWarehouseRequest addProductToWarehouseRequest) throws NoSpecifiedProductInWarehouseException;

    AddressDto getAddress();

    void shipToDelivery(ShippedToDeliveryRequest request);

    void returnProductsToWarehouse(Map<UUID, Long> products);

    BookedProductsDto assembly(AssemblyProductsForOrderRequest orderRequest);
}
