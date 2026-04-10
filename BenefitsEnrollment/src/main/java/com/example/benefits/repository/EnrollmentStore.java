package com.example.benefits.repository;

import com.example.benefits.domain.Enrollment;

import java.util.List;
import java.util.Optional;

public interface EnrollmentStore {

    Enrollment save(Enrollment enrollment);

    boolean existsByEmployeeIdAndPlanId(Long employeeId, Long planId);

    boolean existsByEmployeeIdAndPlanIdAndIdNot(Long employeeId, Long planId, Long enrollmentId);

    List<Long> findPlanIdsByEmployeeId(Long employeeId);

    Optional<Enrollment> findById(Long id);

    List<Enrollment> findAll();

    void deleteById(Long id);

    void deleteByEmployeeId(Long employeeId);

    void deleteByPlanId(Long planId);
}
