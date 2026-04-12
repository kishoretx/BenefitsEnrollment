package com.example.benefits.service;

import com.example.benefits.domain.Plan;
import com.example.benefits.domain.PlanType;
import com.example.benefits.dto.CreatePlanRequest;
import com.example.benefits.dto.PaginatedResponse;
import com.example.benefits.dto.UpdatePlanRequest;
import com.example.benefits.exception.PlanNotFoundException;
import com.example.benefits.repository.PlanStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {

    private final PlanStore planStore;
    private final PaginationHelper paginationHelper;

    public PlanService(PlanStore planStore, PaginationHelper paginationHelper) {
        this.planStore = planStore;
        this.paginationHelper = paginationHelper;
    }

    public Plan createPlan(CreatePlanRequest request) {
        Plan plan = new Plan(request.id(), request.name(), request.type(), request.cost());
        return planStore.save(plan);
    }

    public Plan getPlanOrThrow(Long planId) {
        return planStore.findById(planId)
                .orElseThrow(() -> new PlanNotFoundException(planId));
    }

    public List<Plan> getPlansByIds(List<Long> ids) {
        return planStore.findAllByIds(ids);
    }

    public Plan updatePlan(Long planId, UpdatePlanRequest request) {
        Plan plan = getPlanOrThrow(planId);
        plan.setName(request.name());
        plan.setType(request.type());
        plan.setCost(request.cost());
        return planStore.save(plan);
    }

    public void deletePlan(Long planId) {
        getPlanOrThrow(planId);
        planStore.deleteById(planId);
    }

    public PaginatedResponse<Plan> getPlans(PlanType type, String name, int page, int size) {
        List<Plan> plans = planStore.findAll()
                .stream()
                .filter(plan -> type == null || plan.getType() == type)
                .filter(plan -> name == null || plan.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
        return paginationHelper.paginate(plans, page, size);
    }
}
