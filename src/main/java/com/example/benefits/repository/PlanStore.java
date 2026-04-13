package com.example.benefits.repository;

import com.example.benefits.domain.Plan;

import java.util.List;
import java.util.Optional;

public interface PlanStore {

    Plan save(Plan plan);

    Optional<Plan> findById(Long id);

    List<Plan> findAllByIds(List<Long> ids);

    List<Plan> findAll();

    void deleteById(Long id);

    default long count() {
        return findAll().size();
    }
}
