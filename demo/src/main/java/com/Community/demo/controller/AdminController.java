// src/main/java/com/Community/demo/controller/AdminController.java
package com.Community.demo.controller;

import com.Community.demo.model.Appointment;
import com.Community.demo.model.User;
import com.Community.demo.payload.AdminStats;
import com.Community.demo.services.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) { this.adminService = adminService; }

    @GetMapping("/users")
    public ResponseEntity<List<User>> users() { return ResponseEntity.ok(adminService.listUsers()); }

    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> appointments() {
        return ResponseEntity.ok(adminService.listAppointments());
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStats> stats() { return ResponseEntity.ok(adminService.getStats()); }

    @GetMapping("/export/appointments.csv")
    public void exportAppointments(HttpServletResponse resp) { adminService.exportAppointmentsCsv(resp); }

    @GetMapping("/export/screenings.csv")
    public void exportScreenings(HttpServletResponse resp) { adminService.exportScreeningsCsv(resp); }
}
