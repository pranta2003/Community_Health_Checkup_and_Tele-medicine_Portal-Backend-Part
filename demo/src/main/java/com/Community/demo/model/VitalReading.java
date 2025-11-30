package com.Community.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vital_readings")
// @Data provides getters/setters automatically based on field names
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class VitalReading {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Who the vitals belong to
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "patient_id")
    private User patient;

    // Optional: who recorded it (doctor/nurse)
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "recorded_by_id")
    private User recordedBy;

    // --- VITAL FIELDS ---

    // BP
    private Integer systolic;
    private Integer diastolic;

    // Glucose (KEEPING ONLY ONE DEFINITION)
    private Integer glucoseMgdl;               // e.g., 95 (The internal field name used in the database)
    private String glucoseType;

    // Anthropometrics
    private Double heightCm;
    private Double weightKg;
    private Double bmi;

    private LocalDateTime measuredAt;

    // -----------------------------------------------------------------------
    // CRITICAL CUSTOM SETTER/GETTER FOR DTO COMPATIBILITY (Frontend/Service)
    // -----------------------------------------------------------------------

    /**
     * Custom setter that accepts Double (from DTO) and converts it to Integer
     * for the internal 'glucoseMgdl' field. This resolves the error in HealthServiceImpl.
     * @param glucose The glucose value as a Double.
     */
    public void setGlucose(Double glucose) {
        if (glucose != null) {
            // Convert Double argument to Integer for the private field
            this.glucoseMgdl = glucose.intValue();
        } else {
            this.glucoseMgdl = null;
        }
    }

    /**
     * Custom getter to expose the glucose value as a Double for external/API consumption.
     * @return The glucose value as a Double, or null.
     */
    public Double getGlucose() {
        return this.glucoseMgdl != null ? this.glucoseMgdl.doubleValue() : null;
    }
}