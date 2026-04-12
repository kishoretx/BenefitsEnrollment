package com.example.benefits.dto;

import java.time.LocalDate;

public record EnrollmentResponse(Long id, Long employeeId, Long planId, LocalDate enrollmentDate) {
}
