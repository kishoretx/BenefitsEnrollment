package com.example.benefits.repository.jpa;

import com.example.benefits.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPlanRepository extends JpaRepository<Plan, Long> {
}
