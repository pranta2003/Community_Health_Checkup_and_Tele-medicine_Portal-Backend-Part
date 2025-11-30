package com.Community.demo.controller;

import com.Community.demo.model.VitalReading;
import com.Community.demo.payload.VitalRequest; // <<< ADDED IMPORT
import com.Community.demo.services.HealthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    // HealthController.java (Update saveVital method)

    @PostMapping("/vitals")
    // Use the name 'saveVital' to match the interface method (below)
    public ResponseEntity<VitalReading> saveVital(@RequestBody VitalRequest req) {
        // CRITICAL FIX: The service call name must be 'saveVital'
        return ResponseEntity.ok(healthService.saveVital(req));
    }

    // List by patient
    @GetMapping("/vitals")
    public ResponseEntity<List<VitalReading>> listVitals(@RequestParam("patientId") Long patientId) {
        return ResponseEntity.ok(healthService.listVitalsForPatient(patientId));
    }

    // Get by id
    @GetMapping("/vitals/{id}")
    public ResponseEntity<?> getVital(@PathVariable Long id) {
        return healthService.getVital(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}