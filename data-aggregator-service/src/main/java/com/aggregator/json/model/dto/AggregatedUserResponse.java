package com.aggregator.json.model.dto;

import java.util.List;

public record AggregatedUserResponse(
        Long id,
        String name,
        String username,
        String email,
        AddressDto address,
        String phone,
        String website,
        CompanyDto company,
        List<TodoSummaryDto> todos,
        List<PostSummaryDto> posts
) {
}
