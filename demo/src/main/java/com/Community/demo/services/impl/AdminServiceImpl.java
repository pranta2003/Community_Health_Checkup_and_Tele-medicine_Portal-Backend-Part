package com.Community.demo.services.impl;

import com.Community.demo.model.Appointment;
import com.Community.demo.model.Screening;
import com.Community.demo.model.User;
import com.Community.demo.payload.AdminStats;
import com.Community.demo.repository.AppointmentRepository;
import com.Community.demo.repository.ScreeningRepository;
import com.Community.demo.repository.UserRepository;
import com.Community.demo.services.AdminService;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    // --- 1. UNIFIED FINAL FIELDS ---
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final ScreeningRepository screeningRepository;

    // --- 2. UNIFIED CONSTRUCTOR (Spring uses this one) ---
    public AdminServiceImpl(UserRepository userRepository,
                            AppointmentRepository appointmentRepository,
                            ScreeningRepository screeningRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.screeningRepository = screeningRepository;
    }

    // --- 3. NEW: FIND USERS BY ROLE (Required for DoctorLookupController) ---
    @Override
    public List<User> findUsersByRole(String roleName) {
        // Fetch all users and filter them in memory by role
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles() != null && user.getRoles().contains(roleName))
                // CRITICAL FIX: Collectors import resolved the previous error
                .collect(Collectors.toList());
    }

    // --- 4. EXISTING ADMIN METHODS ---

    @Override
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<Appointment> listAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    public AdminStats getStats() {
        AdminStats s = new AdminStats();
        s.setUserCount((int) userRepository.count());
        s.setAppointmentCount((int) appointmentRepository.count());
        s.setScreeningCount((int) screeningRepository.count());
        return s;
    }

    @Override
    public void exportAppointmentsCsv(HttpServletResponse resp) {
        resp.setContentType("text/csv");
        resp.setHeader("Content-Disposition", "attachment; filename=\"appointments.csv\"");
        try {
            resp.getWriter().write("id,title,scheduledAt,status,patientId,doctorId\n");
            appointmentRepository.findAll().forEach(a -> {
                try {
                    Long pid = a.getPatient() == null ? null : a.getPatient().getId();
                    Long did = a.getDoctor() == null ? null : a.getDoctor().getId();
                    String line = String.format("%d,%s,%s,%s,%s,%s\n",
                            a.getId(),
                            safe(a.getTitle()),
                            a.getScheduledAt() == null ? "" : a.getScheduledAt().toString(),
                            safe(a.getStatus()),
                            pid == null ? "" : pid.toString(),
                            did == null ? "" : did.toString()
                    );
                    resp.getWriter().write(line);
                } catch (IOException e) {
                    // ignore row write error
                }
            });
            resp.getWriter().flush();
        } catch (IOException e) {
            // log if needed
        }
    }

    @Override
    public void exportScreeningsCsv(HttpServletResponse resp) {
        resp.setContentType("text/csv");
        resp.setHeader("Content-Disposition", "attachment; filename=\"screenings.csv\"");
        try {
            resp.getWriter().write("id,userId,takenAt,systolic,diastolic,glucose,glucoseType,heightCm,weightKg,bmi\n");
            screeningRepository.findAll().forEach(s -> {
                try {
                    Long uid = s.getUser() == null ? null : s.getUser().getId();
                    String line = String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                            s.getId(),
                            uid == null ? "" : uid.toString(),
                            s.getTakenAt() == null ? "" : s.getTakenAt().toString(),
                            s.getSystolic(),
                            s.getDiastolic(),
                            s.getGlucose(),
                            safe(s.getGlucoseType()),
                            s.getHeightCm(),
                            s.getWeightKg(),
                            s.getBmi()
                    );
                    resp.getWriter().write(line);
                } catch (IOException e) {
                    // ignore per-row write error
                }
            });
            resp.getWriter().flush();
        } catch (IOException e) {
            // log if needed
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.replaceAll("[\\r\\n,]", " ");
    }
}