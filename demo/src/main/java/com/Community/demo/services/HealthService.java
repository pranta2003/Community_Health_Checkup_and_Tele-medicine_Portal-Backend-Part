package com.Community.demo.services;

import com.Community.demo.model.VitalReading;
import com.Community.demo.payload.VitalRequest; // <<< ADDED IMPORT
import java.util.List;
import java.util.Optional;

public interface HealthService {

    // CRITICAL FIX: Defines the method signature the implementation MUST match
    VitalReading saveVital(VitalRequest req);

    Optional<VitalReading> getVital(Long id);

    List<VitalReading> listVitalsForPatient(Long patientId);
}