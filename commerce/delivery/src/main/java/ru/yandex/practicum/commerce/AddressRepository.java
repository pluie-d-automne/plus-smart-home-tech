package ru.yandex.practicum.commerce;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    Optional<Address> findByCountryAndCityAndStreetAndHouseAndFlat(String country, String city, String street, String House, String flat);
}
