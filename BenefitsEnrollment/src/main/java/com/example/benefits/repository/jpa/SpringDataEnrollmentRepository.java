package com.example.benefits.repository.jpa;

import com.example.benefits.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataEnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByEmployeeIdAndPlanId(Long employeeId, Long planId);

    boolean existsByEmployeeIdAndPlanIdAndIdNot(Long employeeId, Long planId, Long id);

    List<Enrollment> findByEmployeeId(Long employeeId);

    void deleteByEmployeeId(Long employeeId);

    void deleteByPlanId(Long planId);
}
