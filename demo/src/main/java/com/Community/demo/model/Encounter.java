package com.Community.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "encounters")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Encounter {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name="patient_id")
    private User patient;

    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name="doctor_id")
    private User doctor;

    @Column(length = 4000)
    private String notes;                 // free text

    private LocalDateTime createdAt;
}
