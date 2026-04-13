package com.example.benefits.config;

import com.example.benefits.repository.EmployeeStore;
import com.example.benefits.repository.EnrollmentStore;
import com.example.benefits.repository.PlanStore;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class HealthCheckConfig {

    /**
     * Database health check - verifies H2 connection is working
     */
    @Bean
    public HealthIndicator databaseHealthIndicator(DataSource dataSource) {
        return () -> {
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(1)) {
                    return Health.up()
                            .withDetail("database", "H2")
                            .withDetail("connection", "Valid")
                            .build();
                } else {
                    return Health.down()
                            .withDetail("database", "H2")
                            .withDetail("connection", "Invalid")
                            .build();
                }
            } catch (SQLException e) {
                return Health.down()
                        .withDetail("database", "H2")
                        .withDetail("error", e.getMessage())
                        .build();
            }
        };
    }

    /**
     * Employee service health check - verifies employee store is accessible
     */
    @Bean
    public HealthIndicator employeeStoreHealthIndicator(EmployeeStore employeeStore) {
        return () -> {
            try {
                // Try to count employees
                long count = employeeStore.count();
                return Health.up()
                        .withDetail("service", "Employee Store")
                        .withDetail("employeeCount", count)
                        .build();
            } catch (Exception e) {
                return Health.down()
                        .withDetail("service", "Employee Store")
                        .withDetail("error", e.getMessage())
                        .build();
            }
        };
    }

    /**
     * Plan service health check - verifies plan store is accessible
     */
    @Bean
    public HealthIndicator planStoreHealthIndicator(PlanStore planStore) {
        return () -> {
            try {
                long count = planStore.count();
                return Health.up()
                        .withDetail("service", "Plan Store")
                        .withDetail("planCount", count)
                        .build();
            } catch (Exception e) {
                return Health.down()
                        .withDetail("service", "Plan Store")
                        .withDetail("error", e.getMessage())
                        .build();
            }
        };
    }

    /**
     * Enrollment service health check - verifies enrollment store is accessible
     */
    @Bean
    public HealthIndicator enrollmentStoreHealthIndicator(EnrollmentStore enrollmentStore) {
        return () -> {
            try {
                long count = enrollmentStore.count();
                return Health.up()
                        .withDetail("service", "Enrollment Store")
                        .withDetail("enrollmentCount", count)
                        .build();
            } catch (Exception e) {
                return Health.down()
                        .withDetail("service", "Enrollment Store")
                        .withDetail("error", e.getMessage())
                        .build();
            }
        };
    }

    /**
     * Application health check - overall application status
     */
    @Bean
    public HealthIndicator applicationHealthIndicator() {
        return () -> Health.up()
                .withDetail("application", "Benefits Enrollment Service")
                .withDetail("version", "0.0.1-SNAPSHOT")
                .build();
    }
}
