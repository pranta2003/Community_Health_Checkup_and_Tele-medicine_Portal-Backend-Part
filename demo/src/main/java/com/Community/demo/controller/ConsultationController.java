package com.Community.demo.controller;

import com.Community.demo.model.Appointment;
import com.Community.demo.model.ConsultationSession;
import com.Community.demo.model.User;
import com.Community.demo.payload.ConsultationRequest;
import com.Community.demo.payload.ConsultationResponse;
import com.Community.demo.payload.JoinInfoResponse;
import com.Community.demo.repository.AppointmentRepository;
import com.Community.demo.repository.UserRepository;
import com.Community.demo.services.ConsultationService;
import com.Community.demo.services.VideoLinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/consultations")
@Validated
public class ConsultationController {

    private static final Logger log = LoggerFactory.getLogger(ConsultationController.class);

    private final ConsultationService consultationService;
    private final VideoLinkService videoLinkService;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public ConsultationController(ConsultationService consultationService,
                                  VideoLinkService videoLinkService,
                                  UserRepository userRepository,
                                  AppointmentRepository appointmentRepository) {
        this.consultationService = consultationService;
        this.videoLinkService = videoLinkService;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    /** Jitsi join info */
    @GetMapping("/{id}/join-info")
    public ResponseEntity<?> getJoinInfo(@PathVariable("id") Long id) {
        try {
            var opt = consultationService.getSessionById(id);
            if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
            var room = videoLinkService.ensureRoomForConsultation(id);
            var url  = videoLinkService.buildJoinUrl(room);
            return ResponseEntity.ok(new JoinInfoResponse(room, url));
        } catch (Exception ex) {
            log.error("join-info failed", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not build join info: " + ex.getMessage());
        }
    }

    // 🔥 IMPORTANT: supports both /api/consultations and /api/consultations/
    @PostMapping({"", "/"})
    public ResponseEntity<?> createSession(@RequestBody ConsultationRequest req) {
        try {
            // Load doctor & patient from DB
            User doctor = userRepository.findById(req.getDoctorId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));

            User patient = userRepository.findById(req.getPatientId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

            // Appointment is optional
            Appointment appointment = null;
            if (req.getAppointmentId() != null) {
                appointment = appointmentRepository.findById(req.getAppointmentId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));
            }

            // Build entity from DTO
            ConsultationSession session = ConsultationSession.builder()
                    .topic(req.getTopic())
                    .mode(req.getMode())
                    .sessionStart(req.getSessionStart() != null
                            ? LocalDateTime.parse(req.getSessionStart())
                            : null)
                    .sessionEnd(req.getSessionEnd() != null
                            ? LocalDateTime.parse(req.getSessionEnd())
                            : null)
                    .notes(req.getNotes())
                    .prescription(req.getPrescription())
                    .doctor(doctor)
                    .patient(patient)
                    .appointment(appointment)
                    .build();

            // Service will handle jitsiRoom generation if needed
            ConsultationSession saved = consultationService.createSession(session);

            // Return minimal safe DTO
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ConsultationResponse.fromEntity(saved));

        } catch (ResponseStatusException ex) {
            // let Spring handle 404s etc.
            throw ex;
        } catch (Exception ex) {
            log.error("Error creating consultation session: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not create consultation session: " + ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSessionById(@PathVariable("id") Long id) {
        try {
            Optional<ConsultationSession> opt = consultationService.getSessionById(id);
            return opt.<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Consultation session not found with id: " + id));
        } catch (Exception ex) {
            log.error("Error fetching consultation {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching consultation session: " + ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listAllSessions() {
        try {
            List<ConsultationSession> list = consultationService.getAllSessions();
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            log.error("Error listing consultation sessions: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not list consultation sessions: " + ex.getMessage());
        }
    }

    @GetMapping("/upcoming")
    public ResponseEntity<?> listUpcomingSessions() {
        try {
            List<ConsultationSession> list = consultationService.getUpcomingSessions();
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            log.error("Error listing upcoming sessions: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not list upcoming consultation sessions: " + ex.getMessage());
        }
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<?> startSession(@PathVariable("id") Long id) {
        try {
            Optional<ConsultationSession> updated = consultationService.startSession(id);
            return updated.<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Consultation session not found with id: " + id));
        } catch (Exception ex) {
            log.error("Error starting consultation {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not start consultation session: " + ex.getMessage());
        }
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<?> endSession(@PathVariable("id") Long id) {
        try {
            Optional<ConsultationSession> updated = consultationService.endSession(id);
            return updated.<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Consultation session not found with id: " + id));
        } catch (Exception ex) {
            log.error("Error ending consultation {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not end consultation session: " + ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSession(@PathVariable("id") Long id,
                                           @RequestBody ConsultationSession session) {
        try {
            session.setId(id);
            Optional<ConsultationSession> updated = consultationService.updateSession(session);
            return updated.<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Consultation session not found with id: " + id));
        } catch (Exception ex) {
            log.error("Error updating consultation {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not update consultation session: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSession(@PathVariable("id") Long id) {
        try {
            boolean deleted = consultationService.deleteSession(id);
            return deleted
                    ? ResponseEntity.ok("Consultation session deleted")
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Consultation session not found with id: " + id);
        } catch (Exception ex) {
            log.error("Error deleting consultation {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not delete consultation session: " + ex.getMessage());
        }
    }
}
