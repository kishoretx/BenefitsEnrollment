package com.example.benefits.repository.memory;

import com.example.benefits.domain.Employee;
import com.example.benefits.repository.EmployeeStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;

@Repository
@ConditionalOnProperty(name = "app.repository.type", havingValue = "memory", matchIfMissing = true)
public class InMemoryEmployeeRepository implements EmployeeStore {

    private final Map<Long, Employee> employees = new ConcurrentHashMap<>();

    @Override
    public Employee save(Employee employee) {
        employees.put(employee.getId(), employee);
        return employee;
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return Optional.ofNullable(employees.get(id));
    }

    @Override
    public List<Employee> findAll() {
        return employees.values().stream()
                .sorted((left, right) -> left.getId().compareTo(right.getId()))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        employees.remove(id);
    }
}
