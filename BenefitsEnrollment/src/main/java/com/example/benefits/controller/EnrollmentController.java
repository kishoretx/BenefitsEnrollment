package com.example.benefits.controller;

import com.example.benefits.dto.CreateEnrollmentRequest;
import com.example.benefits.dto.PaginatedResponse;
import com.example.benefits.dto.EnrollmentResponse;
import com.example.benefits.dto.UpdateEnrollmentRequest;
import com.example.benefits.service.EnrollmentService;
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

@RestController
@RequestMapping("/enrollments")
@Tag(name = "Enrollments", description = "Enrollment management endpoints")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Enroll an employee in a plan")
    public EnrollmentResponse createEnrollment(@Valid @RequestBody CreateEnrollmentRequest request) {
        return ApiMappings.toResponse(enrollmentService.createEnrollment(request));
    }

    @GetMapping
    @Operation(summary = "Get enrollments with pagination and optional filtering")
    public PaginatedResponse<EnrollmentResponse> getEnrollments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long planId
    ) {
        var result = enrollmentService.getEnrollments(employeeId, planId, page, size);
        return new PaginatedResponse<>(
                result.content().stream().map(ApiMappings::toResponse).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get enrollment by id")
    public EnrollmentResponse getEnrollment(@PathVariable Long id) {
        return ApiMappings.toResponse(enrollmentService.getEnrollmentOrThrow(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an enrollment")
    public EnrollmentResponse updateEnrollment(@PathVariable Long id, @Valid @RequestBody UpdateEnrollmentRequest request) {
        return ApiMappings.toResponse(enrollmentService.updateEnrollment(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an enrollment")
    public void deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
    }
}
