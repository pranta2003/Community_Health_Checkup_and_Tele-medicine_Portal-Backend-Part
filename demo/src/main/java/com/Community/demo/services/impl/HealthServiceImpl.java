package com.Community.demo.services.impl;

import com.Community.demo.model.User;
import com.Community.demo.model.VitalReading;
import com.Community.demo.payload.VitalRequest;
import com.Community.demo.repository.UserRepository;
import com.Community.demo.repository.VitalRepository;
import com.Community.demo.services.HealthService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HealthServiceImpl implements HealthService {

    private final VitalRepository vitalRepository;
    private final UserRepository userRepository; // Ensure this is imported and exists

    public HealthServiceImpl(VitalRepository vitalRepository, UserRepository userRepository) {
        this.vitalRepository = vitalRepository;
        this.userRepository = userRepository;
    }

    // CRITICAL: Signature matches HealthService.java exactly
    @Override
    public VitalReading saveVital(VitalRequest req) {

        // 1. Fetch the User entity (Patient)
        User patient = userRepository.findById(req.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found with ID: " + req.userId()));

        // 2. Map DTO to VitalReading entity
        VitalReading v = new VitalReading();
        v.setPatient(patient); // Set the retrieved patient entity

        // Map all fields from the DTO
        v.setSystolic(req.systolic());
        v.setDiastolic(req.diastolic());

        // Assuming setters in VitalReading are setGlucose and setGlucoseType
        v.setGlucose(req.glucose());
        v.setGlucoseType(req.glucoseType());

        v.setHeightCm(req.heightCm());
        v.setWeightKg(req.weightKg());

        // 3. Handle Timestamp (use current time if not provided by DTO)
        if (req.takenAt() != null) {
            // Assuming the DTO's takenAt is a parsable string (e.g., ISO)
            v.setMeasuredAt(LocalDateTime.parse(req.takenAt()));
        } else {
            v.setMeasuredAt(LocalDateTime.now());
        }

        // 4. Auto BMI calculation (Uses existing good logic)
        if (v.getHeightCm() != null && v.getHeightCm() > 0 &&
                v.getWeightKg() != null && v.getWeightKg() > 0) {
            double hMeters = v.getHeightCm() / 100.0;
            double bmi = v.getWeightKg() / (hMeters * hMeters);
            v.setBmi(Math.round(bmi * 10.0) / 10.0); // 1 decimal
        }

        // 5. Save and return
        return vitalRepository.save(v);
    }

    @Override
    public Optional<VitalReading> getVital(Long id) {
        return vitalRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VitalReading> listVitalsForPatient(Long patientId) {
        return vitalRepository.findByPatientIdOrderByMeasuredAtDesc(patientId);
    }
}