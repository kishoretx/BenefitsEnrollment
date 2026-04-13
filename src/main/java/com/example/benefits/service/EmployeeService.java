package com.example.benefits.service;

import com.example.benefits.domain.Employee;
import com.example.benefits.domain.EmployeeStatus;
import com.example.benefits.dto.CreateEmployeeRequest;
import com.example.benefits.dto.PaginatedResponse;
import com.example.benefits.dto.UpdateEmployeeRequest;
import com.example.benefits.exception.EmployeeNotFoundException;
import com.example.benefits.repository.EmployeeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeStore employeeStore;
    private final PaginationHelper paginationHelper;

    public EmployeeService(EmployeeStore employeeStore, PaginationHelper paginationHelper) {
        this.employeeStore = employeeStore;
        this.paginationHelper = paginationHelper;
    }

    /**
     * Creates a new employee with the provided details.
     *
     * @param request the employee creation request containing employee details
     * @return the created employee entity
     */
    public Employee createEmployee(CreateEmployeeRequest request) {
        logger.info("Creating new employee with ID: {}, name: {}", request.id(), request.name());
        Employee employee = new Employee(request.id(), request.name(), request.status());
        Employee savedEmployee = employeeStore.save(employee);
        logger.info("Successfully created employee with ID: {}", savedEmployee.getId());
        return savedEmployee;
    }

    /**
     * Retrieves an employee by ID or throws an exception if not found.
     *
     * @param employeeId the ID of the employee to retrieve
     * @return the employee entity
     * @throws EmployeeNotFoundException if employee with given ID is not found
     */
    public Employee getEmployeeOrThrow(Long employeeId) {
        logger.info("Retrieving employee with ID: {}", employeeId);
        return employeeStore.findById(employeeId)
                .orElseThrow(() -> {
                    logger.warn("Employee not found with ID: {}", employeeId);
                    return new EmployeeNotFoundException(employeeId);
                });
    }

    /**
     * Updates an existing employee with the provided details.
     *
     * @param employeeId the ID of the employee to update
     * @param request the employee update request containing updated details
     * @return the updated employee entity
     * @throws EmployeeNotFoundException if employee with given ID is not found
     */
    public Employee updateEmployee(Long employeeId, UpdateEmployeeRequest request) {
        logger.info("Updating employee with ID: {}", employeeId);
        Employee employee = getEmployeeOrThrow(employeeId);
        employee.setName(request.name());
        employee.setStatus(request.status());
        Employee updatedEmployee = employeeStore.save(employee);
        logger.info("Successfully updated employee with ID: {}", updatedEmployee.getId());
        return updatedEmployee;
    }

    /**
     * Deletes an employee by ID.
     *
     * @param employeeId the ID of the employee to delete
     * @throws EmployeeNotFoundException if employee with given ID is not found
     */
    public void deleteEmployee(Long employeeId) {
        logger.info("Deleting employee with ID: {}", employeeId);
        getEmployeeOrThrow(employeeId);
        employeeStore.deleteById(employeeId);
        logger.info("Successfully deleted employee with ID: {}", employeeId);
    }

    /**
     * Retrieves a paginated list of employees with optional filtering.
     *
     * @param status the employee status to filter by (can be null)
     * @param name the name substring to filter by (can be null)
     * @param page the page number (0-indexed)
     * @param size the page size
     * @return a paginated response containing employee entities
     */
    public PaginatedResponse<Employee> getEmployees(EmployeeStatus status, String name, int page, int size) {
        logger.info("Retrieving employees with status: {}, name filter: {}, page: {}, size: {}",
                   status, name, page, size);
        List<Employee> employees = employeeStore.findAll()
                .stream()
                .filter(employee -> status == null || employee.getStatus() == status)
                .filter(employee -> name == null || employee.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
        PaginatedResponse<Employee> response = paginationHelper.paginate(employees, page, size);
        logger.info("Retrieved {} employees (page {} of {})",
                   response.content().size(), page, response.totalPages());
        return response;
    }
}
