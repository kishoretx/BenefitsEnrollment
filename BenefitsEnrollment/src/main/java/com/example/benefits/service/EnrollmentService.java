package com.example.benefits.service;

import com.example.benefits.domain.Employee;
import com.example.benefits.domain.EmployeeStatus;
import com.example.benefits.domain.Enrollment;
import com.example.benefits.domain.Plan;
import com.example.benefits.dto.CreateEnrollmentRequest;
import com.example.benefits.dto.PaginatedResponse;
import com.example.benefits.dto.UpdateEnrollmentRequest;
import com.example.benefits.exception.DuplicateEnrollmentException;
import com.example.benefits.exception.EmployeeInactiveException;
import com.example.benefits.exception.EnrollmentNotFoundException;
import com.example.benefits.repository.EnrollmentStore;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentStore enrollmentStore;
    private final EmployeeService employeeService;
    private final PlanService planService;
    private final PaginationHelper paginationHelper;

    public EnrollmentService(EnrollmentStore enrollmentStore, EmployeeService employeeService, PlanService planService,
                             PaginationHelper paginationHelper) {
        this.enrollmentStore = enrollmentStore;
        this.employeeService = employeeService;
        this.planService = planService;
        this.paginationHelper = paginationHelper;
    }

    public Enrollment createEnrollment(CreateEnrollmentRequest request) {
        validateEnrollment(request.employeeId(), request.planId(), null);
        Enrollment enrollment = new Enrollment(request.id(), request.employeeId(), request.planId(), LocalDate.now());
        return saveEnrollment(enrollment);
    }

    public Enrollment updateEnrollment(Long enrollmentId, UpdateEnrollmentRequest request) {
        Enrollment existingEnrollment = getEnrollmentOrThrow(enrollmentId);
        validateEnrollment(request.employeeId(), request.planId(), enrollmentId);
        existingEnrollment.setEmployeeId(request.employeeId());
        existingEnrollment.setPlanId(request.planId());
        existingEnrollment.setEnrollmentDate(request.enrollmentDate() != null ? request.enrollmentDate() : existingEnrollment.getEnrollmentDate());
        return saveEnrollment(existingEnrollment);
    }

    public Enrollment getEnrollmentOrThrow(Long enrollmentId) {
        return enrollmentStore.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentNotFoundException(enrollmentId));
    }

    public void deleteEnrollment(Long enrollmentId) {
        getEnrollmentOrThrow(enrollmentId);
        enrollmentStore.deleteById(enrollmentId);
    }

    public PaginatedResponse<Enrollment> getEnrollments(Long employeeId, Long planId, int page, int size) {
        List<Enrollment> enrollments = enrollmentStore.findAll()
                .stream()
                .filter(enrollment -> employeeId == null || enrollment.getEmployeeId().equals(employeeId))
                .filter(enrollment -> planId == null || enrollment.getPlanId().equals(planId))
                .toList();
        return paginationHelper.paginate(enrollments, page, size);
    }

    public void deleteByEmployeeId(Long employeeId) {
        enrollmentStore.deleteByEmployeeId(employeeId);
    }

    public void deleteByPlanId(Long planId) {
        enrollmentStore.deleteByPlanId(planId);
    }

    public List<Plan> getPlansForEmployee(Long employeeId) {
        employeeService.getEmployeeOrThrow(employeeId);
        List<Long> planIds = enrollmentStore.findPlanIdsByEmployeeId(employeeId);
        return planService.getPlansByIds(planIds);
    }

    private void validateEnrollment(Long employeeId, Long planId, Long enrollmentId) {
        Employee employee = employeeService.getEmployeeOrThrow(employeeId);
        if (employee.getStatus() != EmployeeStatus.ACTIVE) {
            throw new EmployeeInactiveException(employee.getId());
        }

        planService.getPlanOrThrow(planId);

        boolean duplicateExists = enrollmentId == null
                ? enrollmentStore.existsByEmployeeIdAndPlanId(employeeId, planId)
                : enrollmentStore.existsByEmployeeIdAndPlanIdAndIdNot(employeeId, planId, enrollmentId);

        if (duplicateExists) {
            throw new DuplicateEnrollmentException(employeeId, planId);
        }
    }

    private Enrollment saveEnrollment(Enrollment enrollment) {
        try {
            return enrollmentStore.save(enrollment);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateEnrollmentException(enrollment.getEmployeeId(), enrollment.getPlanId());
        }
    }
}
