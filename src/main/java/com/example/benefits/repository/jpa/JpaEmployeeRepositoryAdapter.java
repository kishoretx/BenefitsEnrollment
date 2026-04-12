package com.example.benefits.repository.jpa;

import com.example.benefits.domain.Employee;
import com.example.benefits.repository.EmployeeStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "app.repository.type", havingValue = "h2")
public class JpaEmployeeRepositoryAdapter implements EmployeeStore {

    private final SpringDataEmployeeRepository repository;

    public JpaEmployeeRepositoryAdapter(SpringDataEmployeeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Employee save(Employee employee) {
        return repository.save(employee);
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Employee> findAll() {
        return repository.findAll()
                .stream()
                .sorted((left, right) -> left.getId().compareTo(right.getId()))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
