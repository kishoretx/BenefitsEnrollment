package com.example.benefits.dto;

import jakarta.validation.constraints.NotNull;

public record CreateEnrollmentRequest(
        @NotNull(message = "id is required")
        Long id,
        @NotNull(message = "employeeId is required")
        Long employeeId,
        @NotNull(message = "planId is required")
        Long planId
) {
}
