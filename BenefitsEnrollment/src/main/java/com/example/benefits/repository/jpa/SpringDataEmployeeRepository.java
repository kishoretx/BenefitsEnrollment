package com.example.benefits.repository.jpa;

import com.example.benefits.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataEmployeeRepository extends JpaRepository<Employee, Long> {
}
