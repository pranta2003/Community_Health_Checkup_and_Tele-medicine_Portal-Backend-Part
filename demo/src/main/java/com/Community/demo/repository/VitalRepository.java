package com.Community.demo.repository;

import com.Community.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VitalRepository extends JpaRepository<VitalReading, Long> {
    List<VitalReading> findByPatientIdOrderByMeasuredAtDesc(Long patientId);
}
