package com.Community.demo.controller;

import com.Community.demo.model.User;
import com.Community.demo.services.AuthService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ====================== REGISTER ======================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            User created = authService.register(req.name(), req.email(), req.password());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception ex) {
            log.error("Registration failed", ex);
            return ResponseEntity.badRequest()
                    .body("Registration failed: " + ex.getMessage());
        }
    }

    // ====================== LOGIN ======================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            AuthResponse resp = authService.login(req.email(), req.password());
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            log.warn("Login failed for {}", req.email());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Login failed: " + ex.getMessage());
        }
    }

    // ====================== PROFILE ======================
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User user = authService.getCurrentUser(authHeader);
            return user != null
                    ? ResponseEntity.ok(user)
                    : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        } catch (Exception ex) {
            log.error("Error fetching profile", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not fetch profile");
        }
    }

    // ====================== UPDATE PROFILE ======================
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                           @RequestBody UpdateProfileRequest req) {
        try {
            User updated = authService.updateProfile(authHeader, req.toUser());
            return ResponseEntity.ok(updated);
        } catch (Exception ex) {
            log.error("Error updating profile", ex);
            return ResponseEntity.badRequest().body("Update failed");
        }
    }

    // ====================== CHANGE PASSWORD ======================
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                            @RequestBody ChangePasswordRequest req) {
        try {
            boolean ok = authService.changePassword(authHeader, req.currentPassword(), req.newPassword());
            return ok
                    ? ResponseEntity.ok("Password changed successfully")
                    : ResponseEntity.badRequest().body("Incorrect current password");
        } catch (Exception ex) {
            log.error("Error changing password", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not change password");
        }
    }

    // ====================== LOGOUT ======================
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        authService.logout(authHeader);
        return ResponseEntity.ok("Logged out");
    }

    // ====================== MODERN RECORDS (DTOs) ======================

    public record RegisterRequest(@NotBlank String name,
                                  @NotBlank @Email String email,
                                  @NotBlank String password,
                                  String role) {}

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password) {}

    public record ChangePasswordRequest(
            @NotBlank String currentPassword,
            @NotBlank String newPassword) {}



    public record UpdateProfileRequest(String name, String phone, String address) {
        public User toUser() {
            User u = new User();
            if (name != null) u.setName(name);
            if (phone != null) u.setPhone(phone);
            if (address != null) u.setAddress(address);
            return u;
        }
    }

    public record AuthResponse(String token, User user) {}
}