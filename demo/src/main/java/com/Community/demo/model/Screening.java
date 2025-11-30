// src/main/java/com/Community/demo/model/Screening.java
package com.Community.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name="screenings")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Screening {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id")
    private User user;

    // quick entries
    private Integer systolic;     // BP sys
    private Integer diastolic;    // BP dia
    private String glucoseType;   // "FASTING" or "RANDOM"
    private Double glucose;       // mmol/L (or mg/dL—your choice)
    private Double heightCm;
    private Double weightKg;
    private Double bmi;

    private LocalDateTime takenAt;
}
