package com.example.benefits.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI benefitsEnrollmentOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Benefits Enrollment Service API")
                        .version("v1")
                        .description("REST API for managing employees, plans, and enrollments.")
                        .contact(new Contact().name("Engineering Team"))
                        .license(new License().name("Internal Use")));
    }
}
