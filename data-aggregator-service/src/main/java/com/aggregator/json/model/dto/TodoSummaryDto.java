package com.aggregator.json.model.dto;

public record TodoSummaryDto(
        Long id,
        String title,
        Boolean completed
) {
}
