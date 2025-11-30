package com.Community.demo.services;

import com.Community.demo.model.Encounter;
import com.Community.demo.model.Prescription;
import java.util.List;
import java.util.Optional;

public interface EhrService {
    Encounter saveEncounter(Encounter e);
    List<Encounter> listEncounters(Long patientId);

    Prescription savePrescription(Prescription p);
    List<Prescription> listPrescriptions(Long patientId);
    Optional<Prescription> getPrescription(Long id);

    // Optional: produce PDF bytes (can return null if you skip PDF lib)
    byte[] buildPrescriptionPdf(Prescription p);
}
