package com.Community.demo.services.impl;

import com.Community.demo.model.Encounter;
import com.Community.demo.model.Prescription;
import com.Community.demo.repository.EncounterRepository;
import com.Community.demo.repository.PrescriptionRepository;
import com.Community.demo.services.EhrService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Optional PDF
// If you want PDF, add to pom:
// <dependency>
//   <groupId>com.github.librepdf</groupId><artifactId>openpdf</artifactId><version>1.3.39</version>
// </dependency>

@Service
@Transactional
public class EhrServiceImpl implements EhrService {

    private final EncounterRepository encounterRepo;
    private final PrescriptionRepository rxRepo;

    public EhrServiceImpl(EncounterRepository encounterRepo, PrescriptionRepository rxRepo) {
        this.encounterRepo = encounterRepo;
        this.rxRepo = rxRepo;
    }

    @Override
    public Encounter saveEncounter(Encounter e) {
        if (e.getCreatedAt() == null) e.setCreatedAt(LocalDateTime.now());
        return encounterRepo.save(e);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Encounter> listEncounters(Long patientId) {
        return encounterRepo.findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    @Override
    public Prescription savePrescription(Prescription p) {
        if (p.getCreatedAt() == null) p.setCreatedAt(LocalDateTime.now());
        return rxRepo.save(p);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> listPrescriptions(Long patientId) {
        return rxRepo.findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Prescription> getPrescription(Long id) {
        return rxRepo.findById(id);
    }

    @Override
    public byte[] buildPrescriptionPdf(Prescription p) {
        try {
            // If you don’t want PDF yet, return null here.
            // Minimal OpenPDF implementation:
            com.lowagie.text.Document doc = new com.lowagie.text.Document();
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            com.lowagie.text.pdf.PdfWriter.getInstance(doc, out);
            doc.open();
            doc.add(new com.lowagie.text.Paragraph("Prescription"));
            doc.add(new com.lowagie.text.Paragraph("Patient ID: " + (p.getPatient()!=null?p.getPatient().getId():"")));
            doc.add(new com.lowagie.text.Paragraph("Doctor ID : " + (p.getDoctor()!=null?p.getDoctor().getId():"")));
            doc.add(new com.lowagie.text.Paragraph("Medication: " + p.getMedication()));
            doc.add(new com.lowagie.text.Paragraph("Dose      : " + p.getDose()));
            doc.add(new com.lowagie.text.Paragraph("Frequency : " + p.getFrequency()));
            doc.add(new com.lowagie.text.Paragraph("Days      : " + p.getDays()));
            doc.add(new com.lowagie.text.Paragraph("Date      : " + p.getCreatedAt()));
            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }
}
