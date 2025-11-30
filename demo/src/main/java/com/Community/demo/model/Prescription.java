package com.Community.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Prescription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name="patient_id")
    private User patient;

    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name="doctor_id")
    private User doctor;

    // simple Rx fields
    private String medication;   // e.g., "Paracetamol"
    private String dose;         // e.g., "500 mg"
    private String frequency;    // e.g., "1-0-1"
    private Integer days;        // e.g., 5

    private LocalDateTime createdAt;
}
