package com.example.benefits.repository.jpa;

import com.example.benefits.domain.Enrollment;
import com.example.benefits.repository.EnrollmentStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "app.repository.type", havingValue = "h2")
public class JpaEnrollmentRepositoryAdapter implements EnrollmentStore {

    private final SpringDataEnrollmentRepository repository;

    public JpaEnrollmentRepositoryAdapter(SpringDataEnrollmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Enrollment save(Enrollment enrollment) {
        return repository.save(enrollment);
    }

    @Override
    public boolean existsByEmployeeIdAndPlanId(Long employeeId, Long planId) {
        return repository.existsByEmployeeIdAndPlanId(employeeId, planId);
    }

    @Override
    public boolean existsByEmployeeIdAndPlanIdAndIdNot(Long employeeId, Long planId, Long enrollmentId) {
        return repository.existsByEmployeeIdAndPlanIdAndIdNot(employeeId, planId, enrollmentId);
    }

    @Override
    public List<Long> findPlanIdsByEmployeeId(Long employeeId) {
        return repository.findByEmployeeId(employeeId)
                .stream()
                .map(Enrollment::getPlanId)
                .toList();
    }

    @Override
    public Optional<Enrollment> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Enrollment> findAll() {
        return repository.findAll()
                .stream()
                .sorted((left, right) -> left.getId().compareTo(right.getId()))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteByEmployeeId(Long employeeId) {
        repository.deleteByEmployeeId(employeeId);
    }

    @Override
    public void deleteByPlanId(Long planId) {
        repository.deleteByPlanId(planId);
    }
}
