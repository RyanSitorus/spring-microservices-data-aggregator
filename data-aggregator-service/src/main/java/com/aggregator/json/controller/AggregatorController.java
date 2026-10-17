package com.aggregator.json.controller;

import com.aggregator.json.model.dto.AggregatedUserResponse;
import com.aggregator.json.service.AggregatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class AggregatorController {

    private final AggregatorService aggregatorService;

    public AggregatorController(AggregatorService aggregatorService) {
        this.aggregatorService = aggregatorService;
    }

    @GetMapping({"/api/v1/aggregate", "/aggregate"})
    public ResponseEntity<List<AggregatedUserResponse>> getAllAggregated() {
        return ResponseEntity.ok(aggregatorService.getAggregatedData());
    }

    @GetMapping({"/api/v1/aggregate/{userId}", "/aggregate/{userId}"})
    public ResponseEntity<AggregatedUserResponse> getAggregatedByUser(@PathVariable Long userId) {
        AggregatedUserResponse data = aggregatorService.getAggregatedDataByUserId(userId);
        if (data == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(data);
    }
}
