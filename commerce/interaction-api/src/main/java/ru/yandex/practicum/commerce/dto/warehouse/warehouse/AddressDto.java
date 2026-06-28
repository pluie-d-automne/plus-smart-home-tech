package ru.yandex.practicum.commerce.dto.warehouse.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    String country;
    String city;
    String street;
    String house;
    String flat;
}
