package ru.yandex.practicum.commerce.contract.warehouse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.dto.warehouse.booking.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.booking.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.product.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.product.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.warehouse.AddressDto;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.exception.SpecifiedProductAlreadyInWarehouseException;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseOperations {
    @PutMapping
    void addProduct(@RequestBody NewProductInWarehouseRequest productRequest) throws SpecifiedProductAlreadyInWarehouseException;

    @PostMapping("/check")
    BookedProductsDto checkBookingProducts(@RequestBody ShoppingCartDto shoppingCartDto) throws ProductInShoppingCartLowQuantityInWarehouse;

    @PostMapping("/add")
    void addProductQuantity(@RequestBody AddProductToWarehouseRequest addProductToWarehouseRequest) throws NoSpecifiedProductInWarehouseException;

    @GetMapping("/address")
    AddressDto getAddress();
}
