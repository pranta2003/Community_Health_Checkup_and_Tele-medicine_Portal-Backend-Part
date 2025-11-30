// src/main/java/com/Community/demo/services/AdminService.java
package com.Community.demo.services;

import com.Community.demo.model.Appointment;
import com.Community.demo.model.User;
import com.Community.demo.payload.AdminStats;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public interface AdminService {
    List<User> listUsers();
    List<User> findUsersByRole(String roleName);
    List<Appointment> listAppointments();
    AdminStats getStats();
    void exportAppointmentsCsv(HttpServletResponse resp);
    void exportScreeningsCsv(HttpServletResponse resp);
}
