package com.example.benefits.repository.jpa;

import com.example.benefits.domain.Plan;
import com.example.benefits.repository.PlanStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "app.repository.type", havingValue = "h2")
public class JpaPlanRepositoryAdapter implements PlanStore {

    private final SpringDataPlanRepository repository;

    public JpaPlanRepositoryAdapter(SpringDataPlanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Plan save(Plan plan) {
        return repository.save(plan);
    }

    @Override
    public Optional<Plan> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Plan> findAllByIds(List<Long> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public List<Plan> findAll() {
        return repository.findAll()
                .stream()
                .sorted((left, right) -> left.getId().compareTo(right.getId()))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
