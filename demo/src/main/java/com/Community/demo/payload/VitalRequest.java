// New File: src/main/java/com/Community/demo/payload/VitalRequest.java

package com.Community.demo.payload;

import jakarta.validation.constraints.NotNull;
// If glucose is not always an integer:
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
// import com.Community.demo.model.GlucoseType; // If you have an Enum

public record VitalRequest(
        @NotNull @Min(1) Long userId, // Frontend sends this
        @Min(50) Integer systolic,
        @Min(30) Integer diastolic,
        @DecimalMin("1.0") Double glucose, // Use Double/float for non-integer glucose
        String glucoseType,
        @Min(50) Double heightCm,
        @Min(10) Double weightKg,
        String takenAt // Optional field for date/time
) {
    // Note: The service layer (HealthService) will need to convert this DTO to a VitalReading entity.
}