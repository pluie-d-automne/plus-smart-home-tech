package ru.yandex.practicum.commerce;

import ru.yandex.practicum.commerce.dto.warehouse.booking.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.booking.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.product.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.product.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.warehouse.AddressDto;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.exception.SpecifiedProductAlreadyInWarehouseException;

public interface WarehouseService {
    void addProduct(NewProductInWarehouseRequest productRequest) throws SpecifiedProductAlreadyInWarehouseException;

    BookedProductsDto checkBookingProducts(ShoppingCartDto shoppingCartDto) throws ProductInShoppingCartLowQuantityInWarehouse;

    void addProductQuantity(AddProductToWarehouseRequest addProductToWarehouseRequest) throws NoSpecifiedProductInWarehouseException;

    AddressDto getAddress();
}
