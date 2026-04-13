package com.example.benefits.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "app.repository.type=memory")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BenefitsEnrollmentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateAndRetrieveEmployeePlans() throws Exception {
        createEmployee(1001, "Alice Johnson", "ACTIVE");
        createPlan(2001, "Gold Medical", "MEDICAL", 250.00);
        createPlan(2002, "Vision Plus", "VISION", 40.00);
        createEnrollment(3001, 1001, 2001);
        createEnrollment(3002, 1001, 2002);

        mockMvc.perform(get("/employees/1001/plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2001))
                .andExpect(jsonPath("$[1].id").value(2002));
    }

    @Test
    void shouldSupportPaginationAndFilteringForEmployees() throws Exception {
        createEmployee(1001, "Alice Johnson", "ACTIVE");
        createEmployee(1002, "Bob Stone", "ACTIVE");
        createEmployee(1003, "Carol Davis", "INACTIVE");

        mockMvc.perform(get("/employees")
                        .param("status", "ACTIVE")
                        .param("name", "o")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Alice Johnson"))
                .andExpect(jsonPath("$.content[1].name").value("Bob Stone"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void shouldUpdateAndDeletePlan() throws Exception {
        createPlan(2001, "Gold Medical", "MEDICAL", 250.00);

        mockMvc.perform(put("/plans/2001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Gold Medical Plus",
                                  "type": "MEDICAL",
                                  "cost": 275.00
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gold Medical Plus"))
                .andExpect(jsonPath("$.cost").value(275.0));

        mockMvc.perform(delete("/plans/2001"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/plans/2001"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("PLAN_NOT_FOUND"));
    }

    @Test
    void shouldRejectDuplicateEnrollment() throws Exception {
        createEmployee(1001, "Alice Johnson", "ACTIVE");
        createPlan(2001, "Gold Medical", "MEDICAL", 250.00);
        createEnrollment(3001, 1001, 2001);

        mockMvc.perform(post("/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 3002,
                                  "employeeId": 1001,
                                  "planId": 2001
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("DUPLICATE_ENROLLMENT"));
    }

    @Test
    void shouldRejectInactiveEmployeeEnrollment() throws Exception {
        createEmployee(1003, "Carol Davis", "INACTIVE");
        createPlan(2001, "Gold Medical", "MEDICAL", 250.00);

        mockMvc.perform(post("/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 3004,
                                  "employeeId": 1003,
                                  "planId": 2001
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("EMPLOYEE_INACTIVE"));
    }

    @Test
    void shouldGetSingleEmployee() throws Exception {
        createEmployee(1001, "Alice Johnson", "ACTIVE");

        mockMvc.perform(get("/employees/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1001))
                .andExpect(jsonPath("$.name").value("Alice Johnson"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldUpdateEmployee() throws Exception {
        createEmployee(1001, "Alice Johnson", "ACTIVE");

        mockMvc.perform(put("/employees/1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Alice Smith",
                                  "status": "INACTIVE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Smith"))
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void shouldDeleteEmployee() throws Exception {
        createEmployee(1001, "Alice Johnson", "ACTIVE");

        mockMvc.perform(delete("/employees/1001"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/employees/1001"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("EMPLOYEE_NOT_FOUND"));
    }

    @Test
    void shouldGetPlansWithPagination() throws Exception {
        createPlan(2001, "Gold Medical", "MEDICAL", 250.00);
        createPlan(2002, "Vision Plus", "VISION", 40.00);

        mockMvc.perform(get("/plans")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void shouldGetSinglePlan() throws Exception {
        createPlan(2001, "Gold Medical", "MEDICAL", 250.00);

        mockMvc.perform(get("/plans/2001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2001))
                .andExpect(jsonPath("$.name").value("Gold Medical"))
                .andExpect(jsonPath("$.type").value("MEDICAL"))
                .andExpect(jsonPath("$.cost").value(250.0));
    }

    @Test
    void shouldGetEnrollmentsWithPagination() throws Exception {
        createEmployee(1001, "Alice Johnson", "ACTIVE");
        createPlan(2001, "Gold Medical", "MEDICAL", 250.00);
        createEnrollment(3001, 1001, 2001);

        mockMvc.perform(get("/enrollments")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void shouldGetSingleEnrollment() throws Exception {
        createEmployee(1001, "Alice Johnson", "ACTIVE");
        createPlan(2001, "Gold Medical", "MEDICAL", 250.00);
        createEnrollment(3001, 1001, 2001);

        mockMvc.perform(get("/enrollments/3001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3001))
                .andExpect(jsonPath("$.employeeId").value(1001))
                .andExpect(jsonPath("$.planId").value(2001));
    }

    @Test
    void shouldUpdateEnrollment() throws Exception {
        createEmployee(1001, "Alice Johnson", "ACTIVE");
        createPlan(2001, "Gold Medical", "MEDICAL", 250.00);
        createPlan(2002, "Vision Plus", "VISION", 40.00);
        createEnrollment(3001, 1001, 2001);

        mockMvc.perform(put("/enrollments/3001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "employeeId": 1001,
                                  "planId": 2002
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planId").value(2002));
    }

    @Test
    void shouldDeleteEnrollment() throws Exception {
        createEmployee(1001, "Alice Johnson", "ACTIVE");
        createPlan(2001, "Gold Medical", "MEDICAL", 250.00);
        createEnrollment(3001, 1001, 2001);

        mockMvc.perform(delete("/enrollments/3001"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/enrollments/3001"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("ENROLLMENT_NOT_FOUND"));
    }

    private void createEmployee(long id, String name, String status) throws Exception {
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": %d,
                                  "name": "%s",
                                  "status": "%s"
                                }
                                """.formatted(id, name, status)))
                .andExpect(status().isCreated());
    }

    private void createPlan(long id, String name, String type, double cost) throws Exception {
        mockMvc.perform(post("/plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": %d,
                                  "name": "%s",
                                  "type": "%s",
                                  "cost": %.2f
                                }
                                """.formatted(id, name, type, cost)))
                .andExpect(status().isCreated());
    }

    private void createEnrollment(long id, long employeeId, long planId) throws Exception {
        mockMvc.perform(post("/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": %d,
                                  "employeeId": %d,
                                  "planId": %d
                                }
                                """.formatted(id, employeeId, planId)))
                .andExpect(status().isCreated());
    }
}
