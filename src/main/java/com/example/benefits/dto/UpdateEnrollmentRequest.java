package com.example.benefits.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateEnrollmentRequest(
        @NotNull(message = "employeeId is required")
        Long employeeId,
        @NotNull(message = "planId is required")
        Long planId,
        LocalDate enrollmentDate
) {
}
