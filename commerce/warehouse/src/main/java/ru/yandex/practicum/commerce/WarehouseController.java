package ru.yandex.practicum.commerce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.warehouse.WarehouseOperations;
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

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/warehouse")
@RequiredArgsConstructor
@FeignClient(name = "warehouse", path = "/api/v1/warehouse", fallback = WarehouseNotAvailableFallback.class)
public class WarehouseController implements WarehouseOperations {
    private final WarehouseService warehouseService;

    @Override
    @PutMapping
    public void addProduct(@RequestBody NewProductInWarehouseRequest productRequest) throws SpecifiedProductAlreadyInWarehouseException {
        log.info("Adding new product to warehouse: {}", productRequest);
        warehouseService.addProduct(productRequest);
    }

    @Override
    @PostMapping("/check")
    public BookedProductsDto checkBookingProducts(@RequestBody ShoppingCartDto shoppingCartDto) throws ProductInShoppingCartLowQuantityInWarehouse {
        log.info("Check products for shopping cart: {}", shoppingCartDto);
        BookedProductsDto bookedProductsDto= warehouseService.checkBookingProducts(shoppingCartDto);
        log.info("All products available: {}, bookedProductsDto");
        return bookedProductsDto;
    }

    @Override
    @PostMapping("/add")
    public void addProductQuantity(@RequestBody AddProductToWarehouseRequest addProductToWarehouseRequest) throws NoSpecifiedProductInWarehouseException {
        log.info("Updating product quantity: {}", addProductToWarehouseRequest);
        warehouseService.addProductQuantity(addProductToWarehouseRequest);
    }

    @Override
    @GetMapping("/address")
    public AddressDto getAddress() {
        log.info("Getting warehouse address.");
        AddressDto addressDto = warehouseService.getAddress();
        log.info("Warehouse address: {}", addressDto);
        return addressDto;
    }

    @Override
    @PostMapping("/shipped")
    public void shipToDelivery(@RequestBody ShippedToDeliveryRequest request) {
        log.info("Need to ship to delivery: {}", request);
        warehouseService.shipToDelivery(request);
    }

    @Override
    @PostMapping("/return")
    public void returnProductsToWarehouse(@RequestBody Map<UUID, Long> products) {
        log.info("Need to return products to warehouse: {}", products);
        warehouseService.returnProductsToWarehouse(products);
    }

    @Override
    @PostMapping("/assembly")
    public BookedProductsDto assembly(@RequestBody AssemblyProductsForOrderRequest orderRequest) {
        log.info("Need to assembly order: {}", orderRequest);
        BookedProductsDto bookedProductsDto = warehouseService.assembly(orderRequest);
        log.info("Booked products: {}", bookedProductsDto);
        return bookedProductsDto;
    }
}
