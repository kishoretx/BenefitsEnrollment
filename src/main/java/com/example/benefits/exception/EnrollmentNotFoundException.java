package com.example.benefits.exception;

public class EnrollmentNotFoundException extends RuntimeException {

    public EnrollmentNotFoundException(Long enrollmentId) {
        super("Enrollment not found with id: " + enrollmentId);
    }
}
