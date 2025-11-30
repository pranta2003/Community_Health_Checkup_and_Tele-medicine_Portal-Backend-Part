package com.Community.demo.controller;

import com.Community.demo.model.Appointment;
import com.Community.demo.model.User;
import com.Community.demo.payload.AppointmentRequest;
import com.Community.demo.payload.JoinInfoResponse;
import com.Community.demo.repository.AppointmentRepository;
import com.Community.demo.repository.UserRepository;
import com.Community.demo.services.AppointmentService;
import com.Community.demo.services.VideoLinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private static final Logger log = LoggerFactory.getLogger(AppointmentController.class);

    private final AppointmentService appointmentService;
    private final VideoLinkService videoLinkService;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public AppointmentController(
            AppointmentService appointmentService,
            VideoLinkService videoLinkService,
            UserRepository userRepository,
            AppointmentRepository appointmentRepository
    ) {
        this.appointmentService = appointmentService;
        this.videoLinkService = videoLinkService;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    // Create appointment – no Jitsi room in request body
    @PostMapping("/")
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentRequest req) {

        // 1) Find patient and doctor
        User patient = userRepository.findById(req.getPatientId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        User doctor = null;
        if (req.getDoctorId() != null) {
            doctor = userRepository.findById(req.getDoctorId()).orElse(null);
        }

        // 2) Parse date/time safely
        LocalDateTime scheduledAt = null;
        if (req.getScheduledAt() != null && !req.getScheduledAt().isBlank()) {
            try {
                // Try ISO-8601 first: 2025-12-23T10:00:00
                scheduledAt = LocalDateTime.parse(req.getScheduledAt());
            } catch (DateTimeParseException ex1) {
                try {
                    // Fallback: "23-12-25" (dd-MM-yy) → start of that day
                    LocalDate d = LocalDate.parse(
                            req.getScheduledAt(),
                            DateTimeFormatter.ofPattern("dd-MM-yy")
                    );
                    scheduledAt = d.atStartOfDay();
                } catch (DateTimeParseException ex2) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Invalid scheduledAt format. Use '2025-12-23T10:00:00' or '23-12-25'."
                    );
                }
            }
        }

        // 3) Build the Appointment entity
        Appointment a = Appointment.builder()
                .title(req.getTitle())
                .scheduledAt(scheduledAt) // can be null; service will set default if needed
                .notes(req.getNotes())
                .status(req.getStatus())
                .patient(patient)
                .doctor(doctor)
                .build();

        // 4) Save via service (and send notification inside service)
        Appointment saved = appointmentService.createAppointment(a);

        // 5) Return created appointment
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable("id") String id) {
        try {
            Optional<Appointment> opt = appointmentService.getAppointmentById(id);
            return opt.<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Appointment not found with id: " + id));
        } catch (Exception e) {
            log.error("Error fetching appointment {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching appointment: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> listAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable("id") String id) {
        boolean ok = appointmentService.deleteAppointment(id);
        return ok ? ResponseEntity.ok("Deleted")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found with id: " + id);
    }

    // Still used to get Jitsi join info AFTER appointment is created
    @GetMapping("/{id}/join-info")
    public ResponseEntity<?> getJoinInfo(@PathVariable("id") String id) {
        try {
            var opt = appointmentService.getAppointmentById(id);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found");
            }
            Long apptId = opt.get().getId();
            String room = videoLinkService.ensureRoomForAppointment(apptId);
            String url = videoLinkService.buildJoinUrl(room);
            return ResponseEntity.ok(new JoinInfoResponse(room, url));
        } catch (Exception ex) {
            log.error("Error building join info for appointment {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not build join info");
        }
    }
}
