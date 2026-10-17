package com.aggregator.json.model.dto;

public record AddressDto(
        String street,
        String suite,
        String city,
        String zipcode,
        GeoDto geo
) {
}
