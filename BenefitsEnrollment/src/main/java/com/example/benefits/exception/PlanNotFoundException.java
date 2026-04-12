package com.example.benefits.exception;

public class PlanNotFoundException extends RuntimeException {

    public PlanNotFoundException(Long planId) {
        super("Plan not found with id: " + planId);
    }
}
