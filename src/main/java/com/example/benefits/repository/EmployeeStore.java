package com.example.benefits.repository;

import com.example.benefits.domain.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeStore {

    Employee save(Employee employee);

    Optional<Employee> findById(Long id);

    List<Employee> findAll();

    void deleteById(Long id);

    default long count() {
        return findAll().size();
    }
}
