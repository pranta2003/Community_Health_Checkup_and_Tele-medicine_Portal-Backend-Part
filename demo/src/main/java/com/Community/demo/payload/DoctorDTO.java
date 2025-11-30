package com.Community.demo.payload;

import com.Community.demo.model.User;

public record DoctorDTO(
        Long id,
        String name, // Shows the Doctor's Name (e.g., John Doe)
        String specialty,
        String role,
        boolean isAvailable
) {
    /**
     * Factory method to convert a sensitive User entity into a safe DoctorDTO.
     */
    public static DoctorDTO fromUser(User user, boolean isBusy, String specialty) {

        // CRITICAL: We grab the name property specifically.
        String doctorName = user.getName() != null ? user.getName() : "Unknown Doctor";

        // Normalizes the role string for cleaner display (e.g., ROLE_DOCTOR -> DOCTOR)
        String primaryRole = user.getRoles().stream()
                .findFirst().orElse("ROLE_USER");
        String displayRole = primaryRole.replace("ROLE_", "");

        return new DoctorDTO(
                user.getId(),
                doctorName,
                specialty,
                displayRole,
                !isBusy // isAvailable is the opposite of busy status
        );
    }
}