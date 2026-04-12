package com.example.benefits.controller;

import com.example.benefits.domain.Plan;
import com.example.benefits.dto.CreateEmployeeRequest;
import com.example.benefits.dto.EmployeeResponse;
import com.example.benefits.dto.PaginatedResponse;
import com.example.benefits.dto.PlanResponse;
import com.example.benefits.dto.UpdateEmployeeRequest;
import com.example.benefits.service.EmployeeService;
import com.example.benefits.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.benefits.domain.EmployeeStatus;

@RestController
@RequestMapping("/employees")
@Tag(name = "Employees", description = "Employee management endpoints")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EnrollmentService enrollmentService;

    public EmployeeController(EmployeeService employeeService, EnrollmentService enrollmentService) {
        this.employeeService = employeeService;
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new employee")
    public EmployeeResponse createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        return ApiMappings.toResponse(employeeService.createEmployee(request));
    }

    @GetMapping
    @Operation(summary = "Get employees with pagination and optional filtering")
    public PaginatedResponse<EmployeeResponse> getEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) EmployeeStatus status,
            @RequestParam(required = false) String name
    ) {
        var result = employeeService.getEmployees(status, name, page, size);
        return new PaginatedResponse<>(
                result.content().stream().map(ApiMappings::toResponse).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by id")
    public EmployeeResponse getEmployee(@PathVariable Long id) {
        return ApiMappings.toResponse(employeeService.getEmployeeOrThrow(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an employee")
    public EmployeeResponse updateEmployee(@PathVariable Long id, @Valid @RequestBody UpdateEmployeeRequest request) {
        return ApiMappings.toResponse(employeeService.updateEmployee(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an employee and associated enrollments")
    public void deleteEmployee(@PathVariable Long id) {
        enrollmentService.deleteByEmployeeId(id);
        employeeService.deleteEmployee(id);
    }

    @GetMapping("/{id}/plans")
    @Operation(summary = "Get all plans an employee is enrolled in")
    public java.util.List<PlanResponse> getEmployeePlans(@PathVariable Long id) {
        return enrollmentService.getPlansForEmployee(id)
                .stream()
                .map(ApiMappings::toResponse)
                .toList();
    }
}
