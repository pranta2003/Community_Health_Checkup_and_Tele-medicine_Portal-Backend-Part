package com.Community.demo.services.impl;

import com.Community.demo.model.Appointment;
import com.Community.demo.model.ConsultationSession;
import com.Community.demo.repository.AppointmentRepository;
import com.Community.demo.repository.ConsultationRepository;
import com.Community.demo.repository.UserRepository;
import com.Community.demo.services.ConsultationService;
import com.Community.demo.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ConsultationServiceImpl - implementation for consultation session management.
 *
 * All ID parameters use Long (wrapper) and method signatures match the interface.
 */
@Service
@Transactional
public class ConsultationServiceImpl implements ConsultationService {

    private static final Logger log = LoggerFactory.getLogger(ConsultationServiceImpl.class);

    private final ConsultationRepository consultationRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ConsultationServiceImpl(ConsultationRepository consultationRepository,
                                   AppointmentRepository appointmentRepository,
                                   UserRepository userRepository,
                                   NotificationService notificationService) {
        this.consultationRepository = consultationRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public ConsultationSession createSession(ConsultationSession session) {
        if (session.getMode() == null) session.setMode("ONLINE");
        // auto set a room for ONLINE sessions
        if ("ONLINE".equalsIgnoreCase(session.getMode()) &&
                (session.getJitsiRoom() == null || session.getJitsiRoom().isBlank())) {
            session.setJitsiRoom("chc-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0,16));
        }
        ConsultationSession saved = consultationRepository.save(session);
        log.info("Created consultation session id={}", saved.getId());
        return saved;
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<ConsultationSession> getSessionById(Long id) {
        return consultationRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultationSession> getAllSessions() {
        return consultationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultationSession> getUpcomingSessions() {
        LocalDateTime now = LocalDateTime.now();
        return consultationRepository.findAll().stream()
                .filter(s -> s.getSessionStart() != null && s.getSessionStart().isAfter(now))
                .toList();
    }

    @Override
    public Optional<ConsultationSession> startSession(Long id) {
        Optional<ConsultationSession> opt = consultationRepository.findById(id);
        if (opt.isEmpty()) return Optional.empty();

        ConsultationSession session = opt.get();
        session.setSessionStart(LocalDateTime.now());
        ConsultationSession saved = consultationRepository.save(session);

        // notify patient & doctor (non-blocking)
        try {
            Long patientId = session.getPatient() != null ? session.getPatient().getId() : null;
            Long doctorId  = session.getDoctor()  != null ? session.getDoctor().getId()  : null;

            String title = "Consultation started";
            String msg = "Your consultation (id=" + saved.getId() + ") has started.";

            if (patientId != null) {
                notificationService.createNotification(patientId, title, msg);
            }
            if (doctorId != null) {
                notificationService.createNotification(doctorId, title,
                        "Consultation with patient id=" + (patientId != null ? patientId : "N/A") + " has started.");
            }
        } catch (Exception ex) {
            log.warn("Failed to create notifications for session start id={}, error={}", saved.getId(), ex.getMessage());
        }

        return Optional.of(saved);
    }

    @Override
    public Optional<ConsultationSession> endSession(Long id) {
        Optional<ConsultationSession> opt = consultationRepository.findById(id);
        if (opt.isEmpty()) return Optional.empty();

        ConsultationSession session = opt.get();
        session.setSessionEnd(LocalDateTime.now());
        ConsultationSession saved = consultationRepository.save(session);

        // notify patient & doctor about session end
        try {
            Long patientId = session.getPatient() != null ? session.getPatient().getId() : null;
            Long doctorId  = session.getDoctor()  != null ? session.getDoctor().getId()  : null;

            String title = "Consultation ended";
            String msg = "Your consultation (id=" + saved.getId() + ") has ended.";

            if (patientId != null) {
                notificationService.createNotification(patientId, title, msg);
            }
            if (doctorId != null) {
                notificationService.createNotification(doctorId, title,
                        "Consultation with patient id=" + (patientId != null ? patientId : "N/A") + " has ended.");
            }
        } catch (Exception ex) {
            log.warn("Failed to create notifications for session end id={}, error={}", saved.getId(), ex.getMessage());
        }

        // Optionally: if linked appointment exists, mark appointment as COMPLETED
        try {
            Appointment appt = saved.getAppointment();
            if (appt != null && appt.getId() != null) {
                appointmentRepository.findById(appt.getId()).ifPresent(a -> {
                    a.setStatus("COMPLETED");
                    appointmentRepository.save(a);
                });
            }
        } catch (Exception ex) {
            log.debug("Unable to auto-update linked appointment for consultation id={}", saved.getId());
        }

        return Optional.of(saved);
    }

    @Override
    public Optional<ConsultationSession> updateSession(ConsultationSession session) {
        if (session.getId() == null) return Optional.empty();
        Optional<ConsultationSession> opt = consultationRepository.findById(session.getId());
        if (opt.isEmpty()) return Optional.empty();

        ConsultationSession exist = opt.get();
        if (session.getSessionStart() != null) exist.setSessionStart(session.getSessionStart());
        if (session.getSessionEnd() != null)   exist.setSessionEnd(session.getSessionEnd());
        if (session.getNotes() != null)        exist.setNotes(session.getNotes());
        if (session.getPrescription() != null) exist.setPrescription(session.getPrescription());
        if (session.getMode() != null)         exist.setMode(session.getMode());
        if (session.getDoctor() != null)       exist.setDoctor(session.getDoctor());
        if (session.getPatient() != null)      exist.setPatient(session.getPatient());
        if (session.getAppointment() != null)  exist.setAppointment(session.getAppointment());

        ConsultationSession updated = consultationRepository.save(exist);
        log.info("Updated consultation session id={}", updated.getId());
        return Optional.of(updated);
    }

    @Override
    public boolean deleteSession(Long id) {
        if (!consultationRepository.existsById(id)) return false;
        consultationRepository.deleteById(id);
        log.info("Deleted consultation session id={}", id);
        return true;
    }
}
