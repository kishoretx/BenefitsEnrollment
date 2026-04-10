package com.example.benefits.service;

import com.example.benefits.domain.Employee;
import com.example.benefits.domain.EmployeeStatus;
import com.example.benefits.dto.CreateEmployeeRequest;
import com.example.benefits.dto.PaginatedResponse;
import com.example.benefits.dto.UpdateEmployeeRequest;
import com.example.benefits.exception.EmployeeNotFoundException;
import com.example.benefits.repository.EmployeeStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeStore employeeStore;
    private final PaginationHelper paginationHelper;

    public EmployeeService(EmployeeStore employeeStore, PaginationHelper paginationHelper) {
        this.employeeStore = employeeStore;
        this.paginationHelper = paginationHelper;
    }

    public Employee createEmployee(CreateEmployeeRequest request) {
        Employee employee = new Employee(request.id(), request.name(), request.status());
        return employeeStore.save(employee);
    }

    public Employee getEmployeeOrThrow(Long employeeId) {
        return employeeStore.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
    }

    public Employee updateEmployee(Long employeeId, UpdateEmployeeRequest request) {
        Employee employee = getEmployeeOrThrow(employeeId);
        employee.setName(request.name());
        employee.setStatus(request.status());
        return employeeStore.save(employee);
    }

    public void deleteEmployee(Long employeeId) {
        getEmployeeOrThrow(employeeId);
        employeeStore.deleteById(employeeId);
    }

    public PaginatedResponse<Employee> getEmployees(EmployeeStatus status, String name, int page, int size) {
        List<Employee> employees = employeeStore.findAll()
                .stream()
                .filter(employee -> status == null || employee.getStatus() == status)
                .filter(employee -> name == null || employee.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
        return paginationHelper.paginate(employees, page, size);
    }
}
