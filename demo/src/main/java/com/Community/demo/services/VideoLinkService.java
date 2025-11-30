package com.Community.demo.services;

public interface VideoLinkService {
    /** Returns room name, creating if missing. */
    String ensureRoomForAppointment(Long appointmentId);

    /** Returns room name, creating if missing. */
    String ensureRoomForConsultation(Long consultationId);

    /** Build full https URL to open the room (with safe defaults). */
    String buildJoinUrl(String room);
}
