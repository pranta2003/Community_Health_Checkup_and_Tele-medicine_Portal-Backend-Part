package com.Community.demo.repository;

import com.Community.demo.model.ConsultationSession;
import com.Community.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<ConsultationSession, Long> {

    List<ConsultationSession> findByDoctor(User doctor);
    List<ConsultationSession> findByPatient(User patient);

    List<ConsultationSession> findByDoctorAndSessionStartBetween(
            User doctor, LocalDateTime start, LocalDateTime end);

    List<ConsultationSession> findBySessionEndIsNull();
    List<ConsultationSession> findBySessionEndIsNotNull();

    // CRITICAL FIX: Query to find doctor IDs whose sessions haven't ended (i.e., they are busy/in-progress)
    @Query("SELECT cs.doctor.id FROM ConsultationSession cs WHERE cs.sessionEnd IS NULL")
    List<Long> findBusyDoctorIds();
}