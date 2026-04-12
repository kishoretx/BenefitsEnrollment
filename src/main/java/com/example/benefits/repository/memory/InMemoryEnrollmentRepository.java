package com.example.benefits.repository.memory;

import com.example.benefits.domain.Enrollment;
import com.example.benefits.repository.EnrollmentStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(name = "app.repository.type", havingValue = "memory", matchIfMissing = true)
public class InMemoryEnrollmentRepository implements EnrollmentStore {

    private final Map<Long, Set<Long>> enrollmentsByEmployee = new ConcurrentHashMap<>();
    private final Map<Long, Enrollment> enrollmentDetails = new ConcurrentHashMap<>();

    @Override
    public Enrollment save(Enrollment enrollment) {
        Enrollment existing = enrollmentDetails.get(enrollment.getId());
        if (existing != null) {
            removeRelationship(existing.getEmployeeId(), existing.getPlanId());
        }
        enrollmentsByEmployee.computeIfAbsent(enrollment.getEmployeeId(), key -> ConcurrentHashMap.newKeySet())
                .add(enrollment.getPlanId());
        enrollmentDetails.put(enrollment.getId(), enrollment);
        return enrollment;
    }

    @Override
    public boolean existsByEmployeeIdAndPlanId(Long employeeId, Long planId) {
        return enrollmentsByEmployee.getOrDefault(employeeId, Set.of()).contains(planId);
    }

    @Override
    public boolean existsByEmployeeIdAndPlanIdAndIdNot(Long employeeId, Long planId, Long enrollmentId) {
        return enrollmentDetails.values().stream()
                .anyMatch(enrollment -> !enrollment.getId().equals(enrollmentId)
                        && enrollment.getEmployeeId().equals(employeeId)
                        && enrollment.getPlanId().equals(planId));
    }

    @Override
    public List<Long> findPlanIdsByEmployeeId(Long employeeId) {
        return enrollmentsByEmployee.getOrDefault(employeeId, Set.of())
                .stream()
                .sorted()
                .toList();
    }

    @Override
    public Optional<Enrollment> findById(Long id) {
        return Optional.ofNullable(enrollmentDetails.get(id));
    }

    @Override
    public List<Enrollment> findAll() {
        return enrollmentDetails.values().stream()
                .sorted((left, right) -> left.getId().compareTo(right.getId()))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        Enrollment existing = enrollmentDetails.remove(id);
        if (existing != null) {
            removeRelationship(existing.getEmployeeId(), existing.getPlanId());
        }
    }

    @Override
    public void deleteByEmployeeId(Long employeeId) {
        enrollmentDetails.values().stream()
                .filter(enrollment -> enrollment.getEmployeeId().equals(employeeId))
                .map(Enrollment::getId)
                .toList()
                .forEach(this::deleteById);
    }

    @Override
    public void deleteByPlanId(Long planId) {
        enrollmentDetails.values().stream()
                .filter(enrollment -> enrollment.getPlanId().equals(planId))
                .map(Enrollment::getId)
                .toList()
                .forEach(this::deleteById);
    }

    private void removeRelationship(Long employeeId, Long planId) {
        Set<Long> planIds = enrollmentsByEmployee.get(employeeId);
        if (planIds == null) {
            return;
        }
        planIds.remove(planId);
        if (planIds.isEmpty()) {
            enrollmentsByEmployee.remove(employeeId);
        }
    }
}
