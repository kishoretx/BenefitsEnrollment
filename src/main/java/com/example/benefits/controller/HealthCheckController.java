package com.example.benefits.controller;

import org.springframework.boot.actuate.health.CompositeHealth;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class HealthCheckController {

    private final HealthEndpoint healthEndpoint;

    public HealthCheckController(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @GetMapping("/ui/health")
    public String healthStatus(Model model) {
        HealthComponent health = healthEndpoint.health();
        
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("status", health.getStatus().getCode());
        healthData.put("description", getStatusDescription(health.getStatus()));
        
        Map<String, Map<String, Object>> components = new HashMap<>();
        
        // Cast to CompositeHealth to access components
        if (health instanceof CompositeHealth) {
            CompositeHealth compositeHealth = (CompositeHealth) health;
            for (Map.Entry<String, HealthComponent> entry : compositeHealth.getComponents().entrySet()) {
                Map<String, Object> componentData = new HashMap<>();
                componentData.put("status", entry.getValue().getStatus().getCode());
                
                // Get details if available
                if (entry.getValue() instanceof org.springframework.boot.actuate.health.Health) {
                    var healthComponent = (org.springframework.boot.actuate.health.Health) entry.getValue();
                    componentData.put("details", healthComponent.getDetails());
                }
                
                components.put(entry.getKey(), componentData);
            }
        }
        
        model.addAttribute("healthData", healthData);
        model.addAttribute("components", components);
        
        return "health/status";
    }
    
    private String getStatusDescription(Status status) {
        return switch (status.getCode()) {
            case "UP" -> "All systems operational";
            case "DOWN" -> "Service is experiencing issues";
            case "OUT_OF_SERVICE" -> "Service is temporarily unavailable";
            case "UNKNOWN" -> "Health status is unknown";
            default -> "Status: " + status.getCode();
        };
    }
}
