package com.Community.demo.repository;

import com.Community.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EncounterRepository extends JpaRepository<Encounter, Long> {
    List<Encounter> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}
