package com.aggregator.json.model.dto;

public record ExternalTodoDto(
        Long userId,
        Long id,
        String title,
        Boolean completed
) {
}
