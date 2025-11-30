package com.Community.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * ConsultationSession entity used by ConsultationServiceImpl.
 *
 * Fields and method names match what the service expects:
 *  - getMode()/setMode(...)
 *  - getSessionStart()/setSessionStart(...)
 *  - getSessionEnd()/setSessionEnd(...)
 *  - getNotes()/setNotes(...)
 *  - getPrescription()/setPrescription(...)
 *  - getDoctor()/setDoctor(...)
 *  - getPatient()/setPatient(...)
 *  - getAppointment()/setAppointment(...)
 */
@Entity
@Table(name = "consultation_sessions")
@Data                // Lombok: generates getters/setters, toString, equals/hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;

    /**
     * Mode of the session, e.g. "ONLINE" or "OFFLINE"
     */
    private String mode;

    /**
     * Start and end timestamps for scheduled session
     */
    private LocalDateTime sessionStart;
    private LocalDateTime sessionEnd;

    @Column(length = 2000)
    private String notes;

    @Column(length = 4000)
    private String prescription;

    // relations: keep LAZY to avoid fetching everything by default
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private User doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    // NEW: Jitsi room for this session (if ONLINE)
    @Column(name = "jitsi_room", length = 150)
    private String jitsiRoom;

}
