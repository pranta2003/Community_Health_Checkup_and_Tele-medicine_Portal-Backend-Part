package com.Community.demo.services.impl;

import com.Community.demo.model.Appointment;
import com.Community.demo.model.User;
import com.Community.demo.repository.AppointmentRepository;
import com.Community.demo.repository.UserRepository;
import com.Community.demo.services.AppointmentService;
import com.Community.demo.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  UserRepository userRepository,
                                  NotificationService notificationService) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Appointment createAppointment(Appointment appointment) {
        if (appointment.getStatus() == null) {
            appointment.setStatus("SCHEDULED");
        }
        if (appointment.getScheduledAt() == null) {
            appointment.setScheduledAt(LocalDateTime.now().plusDays(1));
        }

        Appointment saved = appointmentRepository.save(appointment);
        log.info("Appointment saved, id={}", saved.getId());

        try {
            Long patientId = null;
            User patient = saved.getPatient();
            if (patient != null) {
                patientId = patient.getId();
            }

            if (patientId != null) {
                String title = "Appointment booked";
                String msg = "Your appointment '" +
                        (saved.getTitle() == null ? "Appointment" : saved.getTitle()) +
                        "' is scheduled at " + saved.getScheduledAt();

                notificationService.createNotification(patientId, title, msg);
                log.info("Notification created for userId={}", patientId);
            } else {
                log.debug("No patient info found on appointment id={}, skipping notification.", saved.getId());
            }
        } catch (Exception ex) {
            log.warn("Failed to create notification for appointment id={}, error={}",
                    saved.getId(), ex.getMessage());
        }

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Appointment> getAppointmentById(String idStr) {
        try {
            Long id = Long.parseLong(idStr);
            return appointmentRepository.findById(id);
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> getUpcomingAppointments() {
        return appointmentRepository.findAll().stream()
                .filter(a -> a.getScheduledAt() != null && a.getScheduledAt().isAfter(LocalDateTime.now()))
                .toList();
    }

    @Override
    public Optional<Appointment> updateAppointment(Appointment appointment) {
        if (appointment.getId() == null) return Optional.empty();
        Optional<Appointment> opt = appointmentRepository.findById(appointment.getId());
        if (opt.isEmpty()) return Optional.empty();

        Appointment exist = opt.get();
        exist.setTitle(appointment.getTitle());
        exist.setScheduledAt(appointment.getScheduledAt());
        exist.setNotes(appointment.getNotes());
        exist.setStatus(appointment.getStatus());
        if (appointment.getPatient() != null) {
            exist.setPatient(appointment.getPatient());
        }

        Appointment updated = appointmentRepository.save(exist);
        return Optional.of(updated);
    }

    @Override
    public boolean deleteAppointment(String idStr) {
        try {
            Long id = Long.parseLong(idStr);
            if (!appointmentRepository.existsById(id)) return false;
            appointmentRepository.deleteById(id);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
