package com.example.benefits.service;

import com.example.benefits.domain.EmployeeStatus;
import com.example.benefits.domain.PlanType;
import com.example.benefits.dto.CreateEmployeeRequest;
import com.example.benefits.dto.CreateEnrollmentRequest;
import com.example.benefits.dto.CreatePlanRequest;
import com.example.benefits.exception.DuplicateEnrollmentException;
import com.example.benefits.exception.EmployeeInactiveException;
import com.example.benefits.repository.memory.InMemoryEmployeeRepository;
import com.example.benefits.repository.memory.InMemoryEnrollmentRepository;
import com.example.benefits.repository.memory.InMemoryPlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnrollmentServiceTest {

    private EnrollmentService enrollmentService;
    private EmployeeService employeeService;
    private PlanService planService;

    @BeforeEach
    void setUp() {
        PaginationHelper paginationHelper = new PaginationHelper();
        employeeService = new EmployeeService(new InMemoryEmployeeRepository(), paginationHelper);
        planService = new PlanService(new InMemoryPlanRepository(), paginationHelper);
        enrollmentService = new EnrollmentService(new InMemoryEnrollmentRepository(), employeeService, planService, paginationHelper);
    }

    @Test
    void shouldCreateEnrollmentForActiveEmployee() {
        employeeService.createEmployee(new CreateEmployeeRequest(1L, "Alice", EmployeeStatus.ACTIVE));
        planService.createPlan(new CreatePlanRequest(1L, "Gold Health", PlanType.MEDICAL, BigDecimal.valueOf(100.00)));

        assertDoesNotThrow(() -> enrollmentService.createEnrollment(new CreateEnrollmentRequest(1L, 1L, 1L)));
    }

    @Test
    void shouldRejectDuplicateEnrollment() {
        employeeService.createEmployee(new CreateEmployeeRequest(1L, "Alice", EmployeeStatus.ACTIVE));
        planService.createPlan(new CreatePlanRequest(1L, "Gold Health", PlanType.MEDICAL, BigDecimal.valueOf(100.00)));
        enrollmentService.createEnrollment(new CreateEnrollmentRequest(1L, 1L, 1L));

        assertThrows(DuplicateEnrollmentException.class,
                () -> enrollmentService.createEnrollment(new CreateEnrollmentRequest(2L, 1L, 1L)));
    }

    @Test
    void shouldRejectInactiveEmployee() {
        employeeService.createEmployee(new CreateEmployeeRequest(1L, "Bob", EmployeeStatus.INACTIVE));
        planService.createPlan(new CreatePlanRequest(1L, "Vision Plus", PlanType.VISION, BigDecimal.valueOf(50.00)));

        assertThrows(EmployeeInactiveException.class,
                () -> enrollmentService.createEnrollment(new CreateEnrollmentRequest(1L, 1L, 1L)));
    }
}
