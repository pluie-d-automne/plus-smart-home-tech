package ru.yandex.practicum.commerce;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.dto.warehouse.booking.BookedProductsDto;
import ru.yandex.practicum.commerce.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.product.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.product.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.dto.warehouse.warehouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warehouse.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.commerce.dto.warehouse.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.commerce.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.exception.SpecifiedProductAlreadyInWarehouseException;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final OrderBookingRepository orderBookingRepository;
    private final WarehouseProductMapper productMapper;

    private static final String[] ADDRESSES = new String[] {"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    @Override
    public void addProduct(NewProductInWarehouseRequest productRequest) throws SpecifiedProductAlreadyInWarehouseException {
        UUID productId = productRequest.getProductId();
        Optional<WarehouseProduct> productFound = warehouseRepository.findById(productId);

        if (productFound.isPresent()) {
            throw new SpecifiedProductAlreadyInWarehouseException("Товар с таким описанием уже зарегистрирован на складе: " + productFound.get());
        } else {
            WarehouseProduct product = productMapper.fromNewRequest(productRequest);
            log.info("Get product {} from productRequest {}", product, productRequest);
            WarehouseProduct productSaved = warehouseRepository.save(product);
            log.info("Added new product to warehouse: {}", productSaved);
        }
    }

    @Override
    public BookedProductsDto checkBookingProducts(ShoppingCartDto shoppingCartDto) throws ProductInShoppingCartLowQuantityInWarehouse {
        Double deliveryWeight = 0D;
        Double deliveryVolume = 0D;
        Boolean fragile = false;
        Map<UUID, Long> productsToCheck = shoppingCartDto.getProducts();

        for (UUID productId : productsToCheck.keySet()) {
            WarehouseProduct warehouseProduct = warehouseRepository.findById(productId)
                    .orElseThrow(() -> new ProductInShoppingCartLowQuantityInWarehouse(
                            "Product with id=" + productId + " was not found in warehouse"));
            Long requestedQuantity = productsToCheck.get(productId);

            if (warehouseProduct.getQuantity() < productsToCheck.get(productId)) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        "Product " + productId + " only available in quantity of " + warehouseProduct.getQuantity() +
                                ". Requested: " + productsToCheck.get(productId));
            }

            deliveryWeight += requestedQuantity * warehouseProduct.getWeight();
            deliveryVolume += requestedQuantity * warehouseProduct.getWeight() * warehouseProduct.getDepth() * warehouseProduct.getHeight();
            if (warehouseProduct.getFragile()) {
                fragile = true;
            }
        }

        return BookedProductsDto.builder().deliveryWeight(deliveryWeight).deliveryVolume(deliveryVolume).fragile(fragile).build();
    }

    @Override
    public void addProductQuantity(AddProductToWarehouseRequest addProductToWarehouseRequest) throws NoSpecifiedProductInWarehouseException {
        UUID productId = addProductToWarehouseRequest.getProductId();
        Long quantityToAdd = addProductToWarehouseRequest.getQuantity();

        WarehouseProduct warehouseProduct = warehouseRepository.findById(productId)
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(
                        "Product with id=" + productId + " was not found in warehouse"));

        Long currentQuantity = warehouseProduct.getQuantity();
        warehouseProduct.setQuantity(currentQuantity + quantityToAdd);

        WarehouseProduct warehouseProductUpd = warehouseRepository.save(warehouseProduct);
        log.info("Updated product quantity: {}", warehouseProductUpd);
    }

    @Override
    public AddressDto getAddress() {
        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    };

    @Override
    public void shipToDelivery(ShippedToDeliveryRequest request) {
        // обновить информацию о собранном заказе в базе данных склада: добавить в него идентификатор доставки
        log.info("Need to add deliveryId to warehouse order data: {}", request);
        OrderBooking orderBooking = orderBookingRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NoOrderFoundException("Warehouse has no data on order " + request.getOrderId()));
        orderBooking.setDeliveryId(request.getDeliveryId());
        OrderBooking orderBookingUpd = orderBookingRepository.save(orderBooking);
        log.info("Warehouse order booking data updated: {}", orderBookingUpd);
    }

    @Override
    public void returnProductsToWarehouse(Map<UUID, Long> products) {
        // Увеличить кол-во переданных товаров на складе
        for (UUID productId : products.keySet()) {
            Optional<WarehouseProduct> warehouseProduct= warehouseRepository.findById(productId);
            if (warehouseProduct.isPresent()) {
                WarehouseProduct product = warehouseProduct.get();
                product.setQuantity(products.get(productId) + product.getQuantity());
                WarehouseProduct productUpd = warehouseRepository.save(product);
                log.info("Product quantity updated: {}", productUpd);
            } else {
                throw new NoSpecifiedProductInWarehouseException("Try to return unknown product " + productId);
            }
        }
    }

    @Override
    public BookedProductsDto assembly(AssemblyProductsForOrderRequest orderRequest) {
        // Проверяем, что продукты есть на складе в нужном количестве и бронируем их
        List<WarehouseProduct> updatedProducts = new ArrayList<>();
        Double deliveryWeight = 0D;
        Double deliveryVolume = 0D;
        Boolean fragile = false;

        for (UUID productId : orderRequest.getProducts().keySet()) {
            WarehouseProduct warehouseProduct = warehouseRepository.findById(productId)
                    .orElseThrow(() -> new ProductInShoppingCartLowQuantityInWarehouse(
                            "Product with id=" + productId + " was not found in warehouse"));
            Long requestedQuantity = orderRequest.getProducts().get(productId);

            if (warehouseProduct.getQuantity() < requestedQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        "Product " + productId + " only available in quantity of " + warehouseProduct.getQuantity() +
                                ". Requested: " + orderRequest.getProducts().get(productId));
            }

            warehouseProduct.setQuantity(warehouseProduct.getQuantity() - requestedQuantity);
            updatedProducts.add(warehouseProduct);

            deliveryWeight += requestedQuantity * warehouseProduct.getWeight();
            deliveryVolume += requestedQuantity * warehouseProduct.getWeight() * warehouseProduct.getDepth() * warehouseProduct.getHeight();
            if (warehouseProduct.getFragile()) {
                fragile = true;
            }
        }

        warehouseRepository.saveAll(updatedProducts);

        OrderBooking orderBooking = orderBookingRepository.save(OrderBooking.builder()
                .orderId(orderRequest.getOrderId())
                .products(orderRequest.getProducts())
                .build());
        log.info("Booked products: {}", orderBooking);
        return BookedProductsDto.builder().deliveryWeight(deliveryWeight).deliveryVolume(deliveryVolume).fragile(fragile).build();
    }
}
