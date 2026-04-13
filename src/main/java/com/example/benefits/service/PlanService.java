package com.example.benefits.service;

import com.example.benefits.domain.Plan;
import com.example.benefits.domain.PlanType;
import com.example.benefits.dto.CreatePlanRequest;
import com.example.benefits.dto.PaginatedResponse;
import com.example.benefits.dto.UpdatePlanRequest;
import com.example.benefits.exception.PlanNotFoundException;
import com.example.benefits.repository.PlanStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {

    private static final Logger logger = LoggerFactory.getLogger(PlanService.class);

    private final PlanStore planStore;
    private final PaginationHelper paginationHelper;

    public PlanService(PlanStore planStore, PaginationHelper paginationHelper) {
        this.planStore = planStore;
        this.paginationHelper = paginationHelper;
    }

    /**
     * Creates a new plan with the provided details.
     *
     * @param request the plan creation request containing plan details
     * @return the created plan entity
     */
    public Plan createPlan(CreatePlanRequest request) {
        logger.info("Creating new plan with ID: {}, name: {}, type: {}", request.id(), request.name(), request.type());
        Plan plan = new Plan(request.id(), request.name(), request.type(), request.cost());
        Plan savedPlan = planStore.save(plan);
        logger.info("Successfully created plan with ID: {}", savedPlan.getId());
        return savedPlan;
    }

    /**
     * Retrieves a plan by ID or throws an exception if not found.
     *
     * @param planId the ID of the plan to retrieve
     * @return the plan entity
     * @throws PlanNotFoundException if plan with given ID is not found
     */
    public Plan getPlanOrThrow(Long planId) {
        logger.info("Retrieving plan with ID: {}", planId);
        return planStore.findById(planId)
                .orElseThrow(() -> {
                    logger.warn("Plan not found with ID: {}", planId);
                    return new PlanNotFoundException(planId);
                });
    }

    /**
     * Retrieves plans by their IDs.
     *
     * @param ids the list of plan IDs to retrieve
     * @return a list of plan entities
     */
    public List<Plan> getPlansByIds(List<Long> ids) {
        logger.debug("Retrieving plans by IDs: {}", ids);
        List<Plan> plans = planStore.findAllByIds(ids);
        logger.debug("Retrieved {} plans", plans.size());
        return plans;
    }

    /**
     * Updates an existing plan with the provided details.
     *
     * @param planId the ID of the plan to update
     * @param request the plan update request containing updated details
     * @return the updated plan entity
     * @throws PlanNotFoundException if plan with given ID is not found
     */
    public Plan updatePlan(Long planId, UpdatePlanRequest request) {
        logger.info("Updating plan with ID: {}", planId);
        Plan plan = getPlanOrThrow(planId);
        plan.setName(request.name());
        plan.setType(request.type());
        plan.setCost(request.cost());
        Plan updatedPlan = planStore.save(plan);
        logger.info("Successfully updated plan with ID: {}", updatedPlan.getId());
        return updatedPlan;
    }

    /**
     * Deletes a plan by ID.
     *
     * @param planId the ID of the plan to delete
     * @throws PlanNotFoundException if plan with given ID is not found
     */
    public void deletePlan(Long planId) {
        logger.info("Deleting plan with ID: {}", planId);
        getPlanOrThrow(planId);
        planStore.deleteById(planId);
        logger.info("Successfully deleted plan with ID: {}", planId);
    }

    /**
     * Retrieves a paginated list of plans with optional filtering.
     *
     * @param type the plan type to filter by (can be null)
     * @param name the name substring to filter by (can be null)
     * @param page the page number (0-indexed)
     * @param size the page size
     * @return a paginated response containing plan entities
     */
    public PaginatedResponse<Plan> getPlans(PlanType type, String name, int page, int size) {
        logger.info("Retrieving plans with type: {}, name filter: {}, page: {}, size: {}",
                   type, name, page, size);
        List<Plan> plans = planStore.findAll()
                .stream()
                .filter(plan -> type == null || plan.getType() == type)
                .filter(plan -> name == null || plan.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
        PaginatedResponse<Plan> response = paginationHelper.paginate(plans, page, size);
        logger.info("Retrieved {} plans (page {} of {})",
                   response.content().size(), page, response.totalPages());
        return response;
    }
}
