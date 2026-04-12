package com.example.benefits.controller;

import com.example.benefits.dto.CreatePlanRequest;
import com.example.benefits.dto.PaginatedResponse;
import com.example.benefits.dto.PlanResponse;
import com.example.benefits.dto.UpdatePlanRequest;
import com.example.benefits.domain.PlanType;
import com.example.benefits.service.EnrollmentService;
import com.example.benefits.service.PlanService;
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
@RequestMapping("/plans")
@Tag(name = "Plans", description = "Plan management endpoints")
public class PlanController {

    private final PlanService planService;
    private final EnrollmentService enrollmentService;

    public PlanController(PlanService planService, EnrollmentService enrollmentService) {
        this.planService = planService;
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new benefits plan")
    public PlanResponse createPlan(@Valid @RequestBody CreatePlanRequest request) {
        var plan = planService.createPlan(request);
        return ApiMappings.toResponse(plan);
    }

    @GetMapping
    @Operation(summary = "Get plans with pagination and optional filtering")
    public PaginatedResponse<PlanResponse> getPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) PlanType type,
            @RequestParam(required = false) String name
    ) {
        var result = planService.getPlans(type, name, page, size);
        return new PaginatedResponse<>(
                result.content().stream().map(ApiMappings::toResponse).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get plan by id")
    public PlanResponse getPlan(@PathVariable Long id) {
        return ApiMappings.toResponse(planService.getPlanOrThrow(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a benefits plan")
    public PlanResponse updatePlan(@PathVariable Long id, @Valid @RequestBody UpdatePlanRequest request) {
        return ApiMappings.toResponse(planService.updatePlan(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a plan and associated enrollments")
    public void deletePlan(@PathVariable Long id) {
        enrollmentService.deleteByPlanId(id);
        planService.deletePlan(id);
    }
}
