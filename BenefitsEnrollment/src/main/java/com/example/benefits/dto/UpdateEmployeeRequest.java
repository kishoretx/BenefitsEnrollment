package com.example.benefits.dto;

import com.example.benefits.domain.EmployeeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateEmployeeRequest(
        @NotBlank(message = "name is required")
        String name,
        @NotNull(message = "status is required")
        EmployeeStatus status
) {
}
