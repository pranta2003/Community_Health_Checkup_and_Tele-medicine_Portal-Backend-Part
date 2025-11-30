package com.Community.demo.services;

import com.Community.demo.model.Appointment;

import java.util.List;
import java.util.Optional;

public interface AppointmentService {
    Appointment createAppointment(Appointment appointment);
    Optional<Appointment> getAppointmentById(String id);
    List<Appointment> getAllAppointments();
    List<Appointment> getUpcomingAppointments();
    Optional<Appointment> updateAppointment(Appointment appointment);
    boolean deleteAppointment(String id);
}
