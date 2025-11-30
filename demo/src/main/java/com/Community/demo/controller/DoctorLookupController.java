package com.Community.demo.controller;

import com.Community.demo.model.User;
import com.Community.demo.repository.ConsultationRepository;
import com.Community.demo.services.AdminService;
import com.Community.demo.payload.DoctorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
public class DoctorLookupController {

    private final AdminService adminService;
    private final ConsultationRepository consultationRepository;

    public DoctorLookupController(AdminService adminService, ConsultationRepository consultationRepository) {
        this.adminService = adminService;
        this.consultationRepository = consultationRepository;
    }

    /**
     * Endpoint to get a secure list of currently AVAILABLE doctors.
     * Filters out doctors who have active consultation sessions.
     */
    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorDTO>> getAvailableDoctors() {

        // 1. Get all users designated as doctors (using AdminService)
        List<User> doctorUsers = adminService.findUsersByRole("ROLE_DOCTOR");

        if (doctorUsers.isEmpty()) {
            // Return an empty list with a 200 OK status
            return ResponseEntity.ok(Collections.emptyList());
        }

        // 2. Find which doctors are currently busy (sessionEnd is NULL)
        // CRITICAL: busyDoctorIds is now effectively final and correctly initialized.
        final List<Long> busyDoctorIds;
        try {
            busyDoctorIds = consultationRepository.findBusyDoctorIds();
        } catch (Exception e) {
            // If the repository fails, assume DB issue and return 500
            System.err.println("CRITICAL: Consultation check failed. DB or query issue: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }

        // 3. Map User entity to secure DTO, FILTERING by availability
        List<DoctorDTO> availableDoctors = doctorUsers.stream()
                .map(user -> {
                    // Check if the doctor's ID is in the list of busy doctors
                    boolean isBusy = busyDoctorIds.contains(user.getId());

                    // Use the secure DTO mapping, using the proper user.getName()
                    return DoctorDTO.fromUser(user, isBusy, "Cardiology");
                })
                // FINAL FILTER: Only include doctors whose isAvailable flag is TRUE
                .filter(DoctorDTO::isAvailable)
                .collect(Collectors.toList());

        // Return 200 OK with the final, filtered list
        return ResponseEntity.ok(availableDoctors);
    }
}