package com.Community.demo.payload;

import lombok.Data;

@Data
public class AppointmentRequest {
    private String title;
    // Client can send "2025-12-23T10:00:00" OR "23-12-25"
    private String scheduledAt;
    private String notes;
    private String status;
    private Long patientId;
    private Long doctorId;
    // jitsiRoom REMOVED – backend will generate Jitsi link later via /{id}/join-info
}
