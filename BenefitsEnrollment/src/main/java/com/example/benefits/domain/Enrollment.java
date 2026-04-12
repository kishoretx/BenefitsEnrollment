package com.example.benefits.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;

@Entity
@Table(
        name = "enrollments",
        uniqueConstraints = @UniqueConstraint(name = "uk_enrollment_employee_plan", columnNames = {"employee_id", "plan_id"})
)
public class Enrollment {

    @Id
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    public Enrollment() {
    }

    public Enrollment(Long id, Long employeeId, Long planId, LocalDate enrollmentDate) {
        this.id = id;
        this.employeeId = employeeId;
        this.planId = planId;
        this.enrollmentDate = enrollmentDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
}
