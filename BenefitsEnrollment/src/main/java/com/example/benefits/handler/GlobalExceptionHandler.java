package com.example.benefits.handler;

import com.example.benefits.dto.ErrorResponse;
import com.example.benefits.exception.DuplicateEnrollmentException;
import com.example.benefits.exception.EmployeeInactiveException;
import com.example.benefits.exception.EmployeeNotFoundException;
import com.example.benefits.exception.EnrollmentNotFoundException;
import com.example.benefits.exception.PlanNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmployeeNotFound(EmployeeNotFoundException exception) {
        return buildError(HttpStatus.NOT_FOUND, "EMPLOYEE_NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(PlanNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlanNotFound(PlanNotFoundException exception) {
        return buildError(HttpStatus.NOT_FOUND, "PLAN_NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(EnrollmentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEnrollmentNotFound(EnrollmentNotFoundException exception) {
        return buildError(HttpStatus.NOT_FOUND, "ENROLLMENT_NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(EmployeeInactiveException.class)
    public ResponseEntity<ErrorResponse> handleEmployeeInactive(EmployeeInactiveException exception) {
        return buildError(HttpStatus.FORBIDDEN, "EMPLOYEE_INACTIVE", exception.getMessage());
    }

    @ExceptionHandler(DuplicateEnrollmentException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEnrollment(DuplicateEnrollmentException exception) {
        return buildError(HttpStatus.CONFLICT, "DUPLICATE_ENROLLMENT", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return buildError(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", exception.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String errorCode, String message) {
        ErrorResponse response = new ErrorResponse(OffsetDateTime.now(), status.value(), errorCode, message);
        return ResponseEntity.status(status).body(response);
    }
}
