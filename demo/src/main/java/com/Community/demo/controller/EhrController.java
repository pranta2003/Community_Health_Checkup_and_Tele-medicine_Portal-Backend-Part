package com.Community.demo.controller;

import com.Community.demo.model.Encounter;
import com.Community.demo.model.Prescription;
import com.Community.demo.services.EhrService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ehr")
public class EhrController {

    private final EhrService ehrService;

    public EhrController(EhrService ehrService) {
        this.ehrService = ehrService;
    }

    // ----- Encounters -----
    @PostMapping("/encounters")
    public ResponseEntity<Encounter> saveEncounter(@RequestBody Encounter e) {
        return ResponseEntity.ok(ehrService.saveEncounter(e));
    }

    @GetMapping("/encounters")
    public ResponseEntity<List<Encounter>> listEncounters(@RequestParam("patientId") Long patientId) {
        return ResponseEntity.ok(ehrService.listEncounters(patientId));
    }

    // ----- Prescriptions -----
    @PostMapping("/prescriptions")
    public ResponseEntity<Prescription> savePrescription(@RequestBody Prescription p) {
        return ResponseEntity.ok(ehrService.savePrescription(p));
    }

    @GetMapping("/prescriptions")
    public ResponseEntity<List<Prescription>> listPrescriptions(@RequestParam("patientId") Long patientId) {
        return ResponseEntity.ok(ehrService.listPrescriptions(patientId));
    }

    // Optional: PDF
    @GetMapping("/prescriptions/{id}/pdf")
    public ResponseEntity<?> rxPdf(@PathVariable Long id) {
        return ehrService.getPrescription(id)
                .map(p -> {
                    byte[] bytes = ehrService.buildPrescriptionPdf(p);
                    if (bytes == null) return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("PDF disabled");
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDisposition(ContentDisposition.attachment()
                            .filename("prescription-" + id + ".pdf").build());
                    return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
