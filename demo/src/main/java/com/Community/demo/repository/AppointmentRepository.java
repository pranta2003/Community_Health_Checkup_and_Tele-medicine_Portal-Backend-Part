package com.Community.demo.repository;

import com.Community.demo.model.Appointment;
import com.Community.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctor(User doctor);

    List<Appointment> findByPatient(User patient);

    // FIXED: scheduledAt (exact field name from entity)
    List<Appointment> findByDoctorAndScheduledAtBetween(
            User doctor, LocalDateTime start, LocalDateTime end);

    List<Appointment> findByPatientAndScheduledAtAfter(
            User patient, LocalDateTime now);

    List<Appointment> findByDoctorAndScheduledAtBefore(
            User doctor, LocalDateTime now);
}