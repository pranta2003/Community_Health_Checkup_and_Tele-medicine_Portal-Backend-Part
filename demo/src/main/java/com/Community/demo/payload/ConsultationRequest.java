package com.Community.demo.payload;

import lombok.Data;

/**
 * DTO for creating a consultation session.
 * Client sends only IDs and ISO-8601 timestamps.
 */
@Data
public class ConsultationRequest {
    private String topic;
    private String mode;
    private String sessionStart; // e.g. "2025-11-24T21:55:20"
    private String sessionEnd;   // e.g. "2025-11-24T22:25:20"
    private String notes;
    private String prescription;
    private Long doctorId;
    private Long patientId;
    private Long appointmentId;
}
