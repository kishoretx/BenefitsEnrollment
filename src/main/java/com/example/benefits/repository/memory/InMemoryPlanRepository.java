package com.example.benefits.repository.memory;

import com.example.benefits.domain.Plan;
import com.example.benefits.repository.PlanStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(name = "app.repository.type", havingValue = "memory", matchIfMissing = true)
public class InMemoryPlanRepository implements PlanStore {

    private final Map<Long, Plan> plans = new ConcurrentHashMap<>();

    @Override
    public Plan save(Plan plan) {
        plans.put(plan.getId(), plan);
        return plan;
    }

    @Override
    public Optional<Plan> findById(Long id) {
        return Optional.ofNullable(plans.get(id));
    }

    @Override
    public List<Plan> findAllByIds(List<Long> ids) {
        return ids.stream()
                .map(plans::get)
                .filter(plan -> plan != null)
                .toList();
    }

    @Override
    public List<Plan> findAll() {
        return plans.values().stream()
                .sorted((left, right) -> left.getId().compareTo(right.getId()))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        plans.remove(id);
    }
}
