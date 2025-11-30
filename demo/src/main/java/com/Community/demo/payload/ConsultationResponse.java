package com.Community.demo.payload;

import com.Community.demo.model.ConsultationSession;

import java.time.LocalDateTime;

/**
 * Minimal, safe response for a consultation session.
 */
public record ConsultationResponse(
        Long id,
        String topic,
        String mode,
        LocalDateTime sessionStart,
        LocalDateTime sessionEnd,
        String notes,
        String prescription,
        Long doctorId,
        Long patientId,
        Long appointmentId,
        String jitsiRoom
) {
    public static ConsultationResponse fromEntity(ConsultationSession s) {
        return new ConsultationResponse(
                s.getId(),
                s.getTopic(),
                s.getMode(),
                s.getSessionStart(),
                s.getSessionEnd(),
                s.getNotes(),
                s.getPrescription(),
                s.getDoctor() != null ? s.getDoctor().getId() : null,
                s.getPatient() != null ? s.getPatient().getId() : null,
                s.getAppointment() != null ? s.getAppointment().getId() : null,
                s.getJitsiRoom()
        );
    }
}
