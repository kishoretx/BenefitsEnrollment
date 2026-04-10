# Benefits Enrollment Service

## Overview

Benefits Enrollment Service is a Spring Boot 3 REST API for managing employees, plans, and enrollments.

The application now supports:

- create, read, update, and delete operations for employees, plans, and enrollments
- pagination and filtering for list endpoints
- business validation that only `ACTIVE` employees can enroll
- duplicate enrollment prevention using both service checks and an H2 database unique constraint
- switchable repository backends:
  - in-memory `HashMap` repositories
  - H2 database with Spring Data JPA
- global JSON error handling with `@ControllerAdvice`
- generated Swagger/OpenAPI docs plus a static OpenAPI YAML file
- Postman collection and sample data payloads
- unit tests and integration tests
- Docker support

## Tech Stack

- Java 17
- Spring Boot 3.3.2
- Maven
- Spring Web
- Spring Validation
- Spring Data JPA
- H2 Database
- springdoc OpenAPI
- JUnit 5 / MockMvc

## Architecture

Layered architecture is used throughout:

- Controller layer: request handling, validation, response mapping
- Service layer: business rules and orchestration
- Repository layer: swappable persistence implementations

Repository abstraction is handled through:

- `EmployeeStore`
- `PlanStore`
- `EnrollmentStore`

This keeps controller and service code unchanged when switching from memory repositories to H2/JPA.

## Domain Model

### Employee

- `id`
- `name`
- `status` (`ACTIVE`, `INACTIVE`)

### Plan

- `id`
- `name`
- `type` (`MEDICAL`, `DENTAL`, `VISION`, `LIFE`, `RETIREMENT`)
- `cost`

### Enrollment

- `id`
- `employeeId`
- `planId`
- `enrollmentDate`

## Business Rules

- Only `ACTIVE` employees can enroll in plans.
- Employee must exist before enrollment.
- Plan must exist before enrollment.
- Duplicate enrollment for the same `employeeId + planId` is not allowed.
- Deleting an employee deletes associated enrollments.
- Deleting a plan deletes associated enrollments.

## Repository Modes

### Memory Mode

Uses in-memory repositories under `com.example.benefits.repository.memory`.

Configured with:

```yaml
app:
  repository:
    type: memory
```

Backed by:

- `Map<Long, Employee>`
- `Map<Long, Plan>`
- `Map<Long, Set<Long>>`

### H2 / JPA Mode

Uses Spring Data JPA adapters under `com.example.benefits.repository.jpa`.

Configured with:

```yaml
app:
  repository:
    type: h2
```

### How To Switch

Run with memory repositories:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=memory
```

Run with H2/JPA repositories:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

You can also switch the default in [application.yml](/Users/kishorekumar/INTELLIJ_PROJECTS/BenefitsEnrollment/src/main/resources/application.yml).

## API Endpoints

### Employees

- `POST /employees`
- `GET /employees`
- `GET /employees/{id}`
- `PUT /employees/{id}`
- `DELETE /employees/{id}`
- `GET /employees/{id}/plans`

### Plans

- `POST /plans`
- `GET /plans`
- `GET /plans/{id}`
- `PUT /plans/{id}`
- `DELETE /plans/{id}`

### Enrollments

- `POST /enrollments`
- `GET /enrollments`
- `GET /enrollments/{id}`
- `PUT /enrollments/{id}`
- `DELETE /enrollments/{id}`

## Pagination And Filtering

Pagination uses:

- `page` default `0`
- `size` default `10`

### Employee Filters

- `status`
- `name`

Example:

```bash
GET /employees?page=0&size=5&status=ACTIVE&name=Alice
```

### Plan Filters

- `type`
- `name`

Example:

```bash
GET /plans?page=0&size=5&type=MEDICAL&name=Gold
```

### Enrollment Filters

- `employeeId`
- `planId`

Example:

```bash
GET /enrollments?page=0&size=5&employeeId=1001
```

### Paginated Response Format

```json
{
  "content": [],
  "page": 0,
  "size": 10,
  "totalElements": 0,
  "totalPages": 0
}
```

## Sample Requests

### Create Employee

```json
{
  "id": 1001,
  "name": "Alice Johnson",
  "status": "ACTIVE"
}
```

### Update Employee

```json
{
  "name": "Alice Johnson Updated",
  "status": "ACTIVE"
}
```

### Create Plan

```json
{
  "id": 2001,
  "name": "Gold Medical",
  "type": "MEDICAL",
  "cost": 250.00
}
```

### Update Plan

```json
{
  "name": "Gold Medical Plus",
  "type": "MEDICAL",
  "cost": 275.00
}
```

### Create Enrollment

```json
{
  "id": 3001,
  "employeeId": 1001,
  "planId": 2001
}
```

### Update Enrollment

```json
{
  "employeeId": 1001,
  "planId": 2002,
  "enrollmentDate": "2026-04-10"
}
```

## Error Handling

All errors return JSON in this format:

```json
{
  "timestamp": "2026-04-10T15:00:00Z",
  "status": 409,
  "errorCode": "DUPLICATE_ENROLLMENT",
  "message": "Duplicate enrollment detected for employeeId=1001 and planId=2001"
}
```

### Exception Mapping

- `EmployeeNotFoundException` -> `404 NOT_FOUND`
- `PlanNotFoundException` -> `404 NOT_FOUND`
- `EnrollmentNotFoundException` -> `404 NOT_FOUND`
- `EmployeeInactiveException` -> `403 FORBIDDEN`
- `DuplicateEnrollmentException` -> `409 CONFLICT`
- validation failures -> `400 BAD_REQUEST`

## Database Constraint For Duplicate Enrollments

In H2 mode, duplicate enrollments are prevented in two places:

1. Service-layer duplicate check before save
2. Database-level unique constraint on `employee_id + plan_id`

The `Enrollment` entity uses a unique constraint:

- `uk_enrollment_employee_plan`

This protects against duplicate inserts even if concurrent requests bypass the in-memory validation window.

## OpenAPI Documentation

Generated Swagger UI:

- [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Generated OpenAPI JSON:

- [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

Static OpenAPI YAML file:

- [benefits-enrollment-openapi.yaml](/Users/kishorekumar/INTELLIJ_PROJECTS/BenefitsEnrollment/src/main/resources/openapi/benefits-enrollment-openapi.yaml)

## Postman Collection

Import the collection from:

- [BenefitsEnrollment.postman_collection.json](/Users/kishorekumar/INTELLIJ_PROJECTS/BenefitsEnrollment/postman/BenefitsEnrollment.postman_collection.json)

The collection includes seed requests for employees, plans, and enrollments, plus validation scenarios.

## Sample Data

Sample payload files:

- [employees.json](/Users/kishorekumar/INTELLIJ_PROJECTS/BenefitsEnrollment/sample-data/employees.json)
- [plans.json](/Users/kishorekumar/INTELLIJ_PROJECTS/BenefitsEnrollment/sample-data/plans.json)
- [enrollments.json](/Users/kishorekumar/INTELLIJ_PROJECTS/BenefitsEnrollment/sample-data/enrollments.json)

Suggested load order:

1. employees
2. plans
3. enrollments
4. `GET /employees/1001/plans`

## Testing

### Unit Tests

- service-level enrollment rule tests

### Integration Tests

- create and fetch employee plans
- list employees with pagination and filtering
- update and delete a plan
- reject duplicate enrollment
- reject inactive employee enrollment

Run tests with:

```bash
mvn test
```

## Run The Application

### Build

```bash
mvn clean package
```

### Run Locally

```bash
mvn spring-boot:run
```

### Run With H2 Profile

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

### H2 Console

- URL: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- JDBC URL: `jdbc:h2:mem:benefitsdb`
- Username: `sa`
- Password: empty

## Docker Support

Files included:

- [Dockerfile](/Users/kishorekumar/INTELLIJ_PROJECTS/BenefitsEnrollment/Dockerfile)
- [.dockerignore](/Users/kishorekumar/INTELLIJ_PROJECTS/BenefitsEnrollment/.dockerignore)

### Build Docker Image

```bash
docker build -t benefits-enrollment-service .
```

### Run Docker Container

```bash
docker run -p 8080:8080 benefits-enrollment-service
```

### Run Docker Container With H2 Profile

```bash
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=h2 benefits-enrollment-service
```

## Project Structure

```text
src/main/java/com/example/benefits
├── config
├── controller
├── domain
├── dto
├── exception
├── handler
├── repository
│   ├── jpa
│   └── memory
└── service
```

## Important Notes

- IDs are client-provided in this implementation.
- Memory mode resets data on restart.
- H2 mode is also in-memory in the current setup and resets when the process stops.
- Generated Swagger docs are the most up-to-date API reference.
- Controller and service logic stay the same when switching storage backends.

## Future Improvements

- add persistent external database support such as PostgreSQL
- add optimistic locking or versioning
- add authentication and authorization
- add bulk import endpoints for sample data
