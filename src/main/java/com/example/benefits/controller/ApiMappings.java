package com.example.benefits.controller;

import com.example.benefits.domain.Employee;
import com.example.benefits.domain.Enrollment;
import com.example.benefits.domain.Plan;
import com.example.benefits.dto.EmployeeResponse;
import com.example.benefits.dto.EnrollmentResponse;
import com.example.benefits.dto.PlanResponse;

final class ApiMappings {

    private ApiMappings() {
    }

    static EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(employee.getId(), employee.getName(), employee.getStatus());
    }

    static PlanResponse toResponse(Plan plan) {
        return new PlanResponse(plan.getId(), plan.getName(), plan.getType(), plan.getCost());
    }

    static EnrollmentResponse toResponse(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getEmployeeId(),
                enrollment.getPlanId(),
                enrollment.getEnrollmentDate()
        );
    }
}
