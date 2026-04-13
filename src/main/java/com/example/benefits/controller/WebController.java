package com.example.benefits.controller;

import com.example.benefits.domain.EmployeeStatus;
import com.example.benefits.domain.PlanType;
import com.example.benefits.dto.CreateEmployeeRequest;
import com.example.benefits.dto.CreateEnrollmentRequest;
import com.example.benefits.dto.CreatePlanRequest;
import com.example.benefits.dto.UpdateEmployeeRequest;
import com.example.benefits.dto.UpdateEnrollmentRequest;
import com.example.benefits.dto.UpdatePlanRequest;
import com.example.benefits.service.EmployeeService;
import com.example.benefits.service.EnrollmentService;
import com.example.benefits.service.PlanService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class WebController {

    private final EmployeeService employeeService;
    private final PlanService planService;
    private final EnrollmentService enrollmentService;

    public WebController(EmployeeService employeeService, PlanService planService, EnrollmentService enrollmentService) {
        this.employeeService = employeeService;
        this.planService = planService;
        this.enrollmentService = enrollmentService;
    }

    @ModelAttribute("activeTab")
    public String getActiveTab(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.equals("/") || uri.equals("")) return "home";
        if (uri.contains("/employees")) return "employees";
        if (uri.contains("/plans")) return "plans";
        if (uri.contains("/enrollments")) return "enrollments";
        if (uri.contains("/health")) return "health";
        return "";
    }

    // Home page
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // ==================== EMPLOYEES ====================
    @GetMapping("/ui/employees")
    public String listEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) EmployeeStatus status,
            @RequestParam(required = false) String name,
            Model model) {
        var result = employeeService.getEmployees(status, name, page, size);
        model.addAttribute("employees", result.content());
        model.addAttribute("page", result.page());
        model.addAttribute("size", result.size());
        model.addAttribute("totalElements", result.totalElements());
        model.addAttribute("totalPages", result.totalPages());
        model.addAttribute("statusFilter", status);
        model.addAttribute("nameFilter", name);
        model.addAttribute("statuses", EmployeeStatus.values());
        return "employees/list";
    }

    @GetMapping("/ui/employees/new")
    public String newEmployeeForm(Model model) {
        model.addAttribute("employee", new EmployeeFormData());
        model.addAttribute("statuses", EmployeeStatus.values());
        return "employees/form";
    }

    @PostMapping("/ui/employees")
    public String createEmployee(@ModelAttribute("employee") EmployeeFormData formData,
                                 BindingResult result, Model model) {
        if (formData.getName() == null || formData.getName().trim().isEmpty()) {
            result.rejectValue("name", "name.empty", "Name is required");
        }
        if (result.hasErrors()) {
            model.addAttribute("statuses", EmployeeStatus.values());
            return "employees/form";
        }
        var request = new CreateEmployeeRequest(null, formData.getName().trim(), formData.getStatus());
        employeeService.createEmployee(request);
        return "redirect:/ui/employees";
    }

    @GetMapping("/ui/employees/{id}/edit")
    public String editEmployeeForm(@PathVariable Long id, Model model) {
        var employee = employeeService.getEmployeeOrThrow(id);
        var formData = new EmployeeFormData();
        formData.setId(employee.getId());
        formData.setName(employee.getName());
        formData.setStatus(employee.getStatus());
        model.addAttribute("employee", formData);
        model.addAttribute("statuses", EmployeeStatus.values());
        return "employees/edit";
    }

    @PostMapping("/ui/employees/{id}")
    public String updateEmployee(@PathVariable Long id,
                                  @ModelAttribute("employee") EmployeeFormData formData,
                                  BindingResult result, Model model) {
        if (formData.getName() == null || formData.getName().trim().isEmpty()) {
            result.rejectValue("name", "name.empty", "Name is required");
        }
        if (result.hasErrors()) {
            model.addAttribute("statuses", EmployeeStatus.values());
            return "employees/edit";
        }
        var request = new UpdateEmployeeRequest(formData.getName().trim(), formData.getStatus());
        employeeService.updateEmployee(id, request);
        return "redirect:/ui/employees";
    }

    @PostMapping("/ui/employees/{id}/delete")
    public String deleteEmployee(@PathVariable Long id) {
        enrollmentService.deleteByEmployeeId(id);
        employeeService.deleteEmployee(id);
        return "redirect:/ui/employees";
    }

    // ==================== PLANS ====================
    @GetMapping("/ui/plans")
    public String listPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) PlanType type,
            @RequestParam(required = false) String name,
            Model model) {
        var result = planService.getPlans(type, name, page, size);
        model.addAttribute("plans", result.content());
        model.addAttribute("page", result.page());
        model.addAttribute("size", result.size());
        model.addAttribute("totalElements", result.totalElements());
        model.addAttribute("totalPages", result.totalPages());
        model.addAttribute("typeFilter", type);
        model.addAttribute("nameFilter", name);
        model.addAttribute("types", PlanType.values());
        return "plans/list";
    }

    @GetMapping("/ui/plans/new")
    public String newPlanForm(Model model) {
        model.addAttribute("plan", new PlanFormData());
        model.addAttribute("types", PlanType.values());
        return "plans/form";
    }

    @PostMapping("/ui/plans")
    public String createPlan(@ModelAttribute("plan") PlanFormData formData,
                             BindingResult result, Model model) {
        if (formData.getName() == null || formData.getName().trim().isEmpty()) {
            result.rejectValue("name", "name.empty", "Name is required");
        }
        if (formData.getCost() == null) {
            result.rejectValue("cost", "cost.null", "Cost is required");
        }
        if (result.hasErrors()) {
            model.addAttribute("types", PlanType.values());
            return "plans/form";
        }
        var request = new CreatePlanRequest(null, formData.getName().trim(), formData.getType(), formData.getCost());
        planService.createPlan(request);
        return "redirect:/ui/plans";
    }

    @GetMapping("/ui/plans/{id}/edit")
    public String editPlanForm(@PathVariable Long id, Model model) {
        var plan = planService.getPlanOrThrow(id);
        var formData = new PlanFormData();
        formData.setId(plan.getId());
        formData.setName(plan.getName());
        formData.setType(plan.getType());
        formData.setCost(plan.getCost());
        model.addAttribute("plan", formData);
        model.addAttribute("types", PlanType.values());
        return "plans/edit";
    }

    @PostMapping("/ui/plans/{id}")
    public String updatePlan(@PathVariable Long id,
                              @ModelAttribute("plan") PlanFormData formData,
                              BindingResult result, Model model) {
        if (formData.getName() == null || formData.getName().trim().isEmpty()) {
            result.rejectValue("name", "name.empty", "Name is required");
        }
        if (result.hasErrors()) {
            model.addAttribute("types", PlanType.values());
            return "plans/edit";
        }
        var request = new UpdatePlanRequest(formData.getName().trim(), formData.getType(), formData.getCost());
        planService.updatePlan(id, request);
        return "redirect:/ui/plans";
    }

    @PostMapping("/ui/plans/{id}/delete")
    public String deletePlan(@PathVariable Long id) {
        enrollmentService.deleteByPlanId(id);
        planService.deletePlan(id);
        return "redirect:/ui/plans";
    }

    // ==================== ENROLLMENTS ====================
    @GetMapping("/ui/enrollments")
    public String listEnrollments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long planId,
            Model model) {
        var result = enrollmentService.getEnrollments(employeeId, planId, page, size);
        model.addAttribute("enrollments", result.content());
        model.addAttribute("page", result.page());
        model.addAttribute("size", result.size());
        model.addAttribute("totalElements", result.totalElements());
        model.addAttribute("totalPages", result.totalPages());
        model.addAttribute("employeeIdFilter", employeeId);
        model.addAttribute("planIdFilter", planId);
        model.addAttribute("employees", employeeService.getEmployees(null, null, 0, 1000).content());
        model.addAttribute("plans", planService.getPlans(null, null, 0, 1000).content());
        return "enrollments/list";
    }

    @GetMapping("/ui/enrollments/new")
    public String newEnrollmentForm(Model model) {
        model.addAttribute("enrollment", new EnrollmentFormData());
        model.addAttribute("employees", employeeService.getEmployees(null, null, 0, 1000).content());
        model.addAttribute("plans", planService.getPlans(null, null, 0, 1000).content());
        return "enrollments/form";
    }

    @PostMapping("/ui/enrollments")
    public String createEnrollment(@ModelAttribute("enrollment") EnrollmentFormData formData,
                                   BindingResult result, Model model) {
        if (formData.getEmployeeId() == null) {
            result.rejectValue("employeeId", "employeeId.null", "Employee is required");
        }
        if (formData.getPlanId() == null) {
            result.rejectValue("planId", "planId.null", "Plan is required");
        }
        if (result.hasErrors()) {
            model.addAttribute("employees", employeeService.getEmployees(null, null, 0, 1000).content());
            model.addAttribute("plans", planService.getPlans(null, null, 0, 1000).content());
            return "enrollments/form";
        }
        var request = new CreateEnrollmentRequest(null, formData.getEmployeeId(), formData.getPlanId());
        enrollmentService.createEnrollment(request);
        return "redirect:/ui/enrollments";
    }

    @GetMapping("/ui/enrollments/{id}/edit")
    public String editEnrollmentForm(@PathVariable Long id, Model model) {
        var enrollment = enrollmentService.getEnrollmentOrThrow(id);
        var formData = new EnrollmentFormData();
        formData.setId(enrollment.getId());
        formData.setEmployeeId(enrollment.getEmployeeId());
        formData.setPlanId(enrollment.getPlanId());
        formData.setEnrollmentDate(enrollment.getEnrollmentDate());
        model.addAttribute("enrollment", formData);
        model.addAttribute("employees", employeeService.getEmployees(null, null, 0, 1000).content());
        model.addAttribute("plans", planService.getPlans(null, null, 0, 1000).content());
        return "enrollments/edit";
    }

    @PostMapping("/ui/enrollments/{id}")
    public String updateEnrollment(@PathVariable Long id,
                                    @ModelAttribute("enrollment") EnrollmentFormData formData,
                                    BindingResult result, Model model) {
        if (formData.getEmployeeId() == null) {
            result.rejectValue("employeeId", "employeeId.null", "Employee is required");
        }
        if (formData.getPlanId() == null) {
            result.rejectValue("planId", "planId.null", "Plan is required");
        }
        if (result.hasErrors()) {
            model.addAttribute("employees", employeeService.getEmployees(null, null, 0, 1000).content());
            model.addAttribute("plans", planService.getPlans(null, null, 0, 1000).content());
            return "enrollments/edit";
        }
        var request = new UpdateEnrollmentRequest(formData.getEmployeeId(), formData.getPlanId(), formData.getEnrollmentDate());
        enrollmentService.updateEnrollment(id, request);
        return "redirect:/ui/enrollments";
    }

    @PostMapping("/ui/enrollments/{id}/delete")
    public String deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
        return "redirect:/ui/enrollments";
    }

    // ==================== Form Data Classes ====================
    public static class EmployeeFormData {
        private Long id;
        private String name;
        private EmployeeStatus status = EmployeeStatus.ACTIVE;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public EmployeeStatus getStatus() { return status; }
        public void setStatus(EmployeeStatus status) { this.status = status; }
    }

    public static class PlanFormData {
        private Long id;
        private String name;
        private PlanType type = PlanType.MEDICAL;
        private BigDecimal cost;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public PlanType getType() { return type; }
        public void setType(PlanType type) { this.type = type; }
        public BigDecimal getCost() { return cost; }
        public void setCost(BigDecimal cost) { this.cost = cost; }
    }

    public static class EnrollmentFormData {
        private Long id;
        private Long employeeId;
        private Long planId;
        private LocalDate enrollmentDate = LocalDate.now();

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getEmployeeId() { return employeeId; }
        public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
        public Long getPlanId() { return planId; }
        public void setPlanId(Long planId) { this.planId = planId; }
        public LocalDate getEnrollmentDate() { return enrollmentDate; }
        public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    }
}
