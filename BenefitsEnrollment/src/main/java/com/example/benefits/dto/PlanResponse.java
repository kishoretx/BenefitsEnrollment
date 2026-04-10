package com.example.benefits.dto;

import com.example.benefits.domain.PlanType;

import java.math.BigDecimal;

public record PlanResponse(Long id, String name, PlanType type, BigDecimal cost) {
}
