package ru.yandex.practicum.commerce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.contract.shopping.order.OrderOperations;
import ru.yandex.practicum.commerce.contract.warehouse.WarehouseOperations;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryState;
import ru.yandex.practicum.commerce.dto.shopping.order.OrderDto;
import ru.yandex.practicum.commerce.dto.warehouse.warehouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warehouse.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.commerce.exception.NoDeliveryFoundException;
import ru.yandex.practicum.commerce.exception.NoWarehouseFound;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryMapper deliveryMapper;
    private final DeliveryRepository deliveryRepository;
    private final AddressRepository addressRepository;
    private final OrderOperations orderOperations;
    private final WarehouseOperations warehouseOperations;

    private static final Double BASE_COST = 5d;

    @Override
    @Transactional
    public DeliveryDto createNewDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = deliveryMapper.fromDto(deliveryDto);
        delivery.setFromAddress(getAddress(deliveryDto.getFromAddress()));
        delivery.setToAddress(getAddress(deliveryDto.getToAddress()));
        Delivery deliverySaved = deliveryRepository.save(delivery);
        return deliveryMapper.toDto(deliverySaved);
    }

    @Override
    @Transactional
    public void deliverySuccess(UUID orderId) throws NoDeliveryFoundException {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("Couldn't find delivery for order " + orderId));

        delivery.setDeliveryState(DeliveryState.DELIVERED);
        Delivery deliveryUpd = deliveryRepository.save(delivery);

        if (deliveryUpd.getDeliveryState().equals(DeliveryState.DELIVERED)) {
            log.info("Order {} was successfully delivered: {}", orderId, deliveryUpd);
        } else {
            log.warn("Could not finalize delivery for order {}: {}", orderId, deliveryUpd);
        }
        orderOperations.deliver(orderId);
    }

    @Override
    @Transactional
    public void deliveryPicked(UUID orderId) throws NoDeliveryFoundException {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("Couldn't find delivery for order " + orderId));

        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        Delivery deliveryUpd = deliveryRepository.save(delivery);

        if (deliveryUpd.getDeliveryState().equals(DeliveryState.IN_PROGRESS)) {
            log.info("Order {} was picked by delivery service: {}", orderId, deliveryUpd);
        } else {
            log.warn("Could not set delivery as picked for order {}: {}", orderId, deliveryUpd);
        }
        warehouseOperations.shipToDelivery(ShippedToDeliveryRequest.builder()
                        .deliveryId(delivery.getDeliveryId())
                        .orderId(orderId)
                        .build());
    }

    @Override
    @Transactional
    public void deliveryFail(UUID orderId) throws NoDeliveryFoundException {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("Couldn't find delivery for order " + orderId));

        delivery.setDeliveryState(DeliveryState.FAILED);
        Delivery deliveryUpd = deliveryRepository.save(delivery);

        if (deliveryUpd.getDeliveryState().equals(DeliveryState.FAILED)) {
            log.info("Order {} was successfully failed: {}", orderId, deliveryUpd);
        } else {
            log.warn("Could not set delivery as failed for order {}: {}", orderId, deliveryUpd);
        }
        orderOperations.failDelivery(orderId);
    }

    @Override
    @Transactional
    public Double deliveryCostCalc(OrderDto orderDto) throws NoDeliveryFoundException {
        Delivery delivery = deliveryRepository.findByOrderId(orderDto.getOrderId())
                .orElseThrow(() -> new NoDeliveryFoundException("Couldn't find delivery for order " + orderDto.getOrderId()));

        Address fromAddress = delivery.getFromAddress();
        Address toAddress = delivery.getToAddress();

        Double deliveryCost = switch(fromAddress.getCountry()) {
            case "ADDRESS_1" -> BASE_COST;
            case "ADDRESS_2" -> BASE_COST * 2;
            default -> throw new NoWarehouseFound("Warehouse with such address does not exist");
        } + BASE_COST;

        if (orderDto.getFragile()) {
            deliveryCost = deliveryCost+0.2 + deliveryCost;
        }

        deliveryCost += orderDto.getDeliveryWeight() * 0.3;
        deliveryCost += orderDto.getDeliveryVolume() * 0.2;

        if (!(fromAddress.getCountry().equals(toAddress.getCountry()) &&
                fromAddress.getCity().equals(toAddress.getCity()) &&
                fromAddress.getStreet().equals(toAddress.getStreet()))) {
            log.info("Улица доставки не совпадает с улицей склада. Увеличиваем стоимость доставки.");
            deliveryCost += deliveryCost*0.2 + deliveryCost;
        }

        delivery.setFragile(orderDto.getFragile());
        delivery.setDeliveryVolume(orderDto.getDeliveryVolume());
        delivery.setDeliveryWeight(orderDto.getDeliveryWeight());
        delivery.setDeliveryCost(deliveryCost);
        deliveryRepository.save(delivery);

        return deliveryCost;
    }


    private Address getAddress (AddressDto addressDto) {
        Optional<Address> addressFound = addressRepository.findByCountryAndCityAndStreetAndHouseAndFlat(addressDto.getCountry(),
                addressDto.getCity(), addressDto.getStreet(), addressDto.getHouse(), addressDto.getFlat());

        if (addressFound.isPresent()) {
            return addressFound.get();
        } else {
            return  addressRepository.save(Address.builder()
                    .country(addressDto.getCountry())
                    .city(addressDto.getCity())
                    .street(addressDto.getStreet())
                    .house(addressDto.getHouse())
                    .flat(addressDto.getFlat())
                    .build());
        }

    }
}
