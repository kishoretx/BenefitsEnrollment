package com.example.benefits.exception;

public class EmployeeInactiveException extends RuntimeException {

    public EmployeeInactiveException(Long employeeId) {
        super("Employee is inactive and cannot enroll in plans. employeeId=" + employeeId);
    }
}
