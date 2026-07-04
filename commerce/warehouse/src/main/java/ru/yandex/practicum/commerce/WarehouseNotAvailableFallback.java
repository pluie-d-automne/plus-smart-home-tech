package ru.yandex.practicum.commerce;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.contract.warehouse.WarehouseOperations;
import ru.yandex.practicum.commerce.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.booking.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.warehouse.product.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.product.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.warehouse.AddressDto;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.exception.ServiceTemporaryUnavailable;
import ru.yandex.practicum.commerce.exception.SpecifiedProductAlreadyInWarehouseException;

@Component
public class WarehouseNotAvailableFallback  implements WarehouseOperations {
    @PutMapping
    public void addProduct(@RequestBody NewProductInWarehouseRequest productRequest) throws SpecifiedProductAlreadyInWarehouseException {
        throw new ServiceTemporaryUnavailable("Warehouse is temporary unavailable.");
    }

    @PostMapping("/check")
    public BookedProductsDto checkBookingProducts(@RequestBody ShoppingCartDto shoppingCartDto) throws ProductInShoppingCartLowQuantityInWarehouse{
        throw new ServiceTemporaryUnavailable("Warehouse is temporary unavailable.");
    }

    @PostMapping("/add")
    public void addProductQuantity(@RequestBody AddProductToWarehouseRequest addProductToWarehouseRequest) throws NoSpecifiedProductInWarehouseException{
        throw new ServiceTemporaryUnavailable("Warehouse is temporary unavailable.");
    }

    @GetMapping("/address")
    public AddressDto getAddress(){
        throw new ServiceTemporaryUnavailable("Warehouse is temporary unavailable.");
    }
}
