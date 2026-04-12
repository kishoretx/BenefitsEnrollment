package com.example.benefits.dto;

import com.example.benefits.domain.EmployeeStatus;

public record EmployeeResponse(Long id, String name, EmployeeStatus status) {
}
