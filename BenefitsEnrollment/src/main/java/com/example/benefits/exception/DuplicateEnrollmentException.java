package com.example.benefits.exception;

public class DuplicateEnrollmentException extends RuntimeException {

    public DuplicateEnrollmentException(Long employeeId, Long planId) {
        super("Duplicate enrollment detected for employeeId=" + employeeId + " and planId=" + planId);
    }
}
