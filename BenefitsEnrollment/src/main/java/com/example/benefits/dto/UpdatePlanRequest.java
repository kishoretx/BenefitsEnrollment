package com.example.benefits.dto;

import com.example.benefits.domain.PlanType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdatePlanRequest(
        @NotBlank(message = "name is required")
        String name,
        @NotNull(message = "type is required")
        PlanType type,
        @NotNull(message = "cost is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "cost must be greater than zero")
        BigDecimal cost
) {
}
