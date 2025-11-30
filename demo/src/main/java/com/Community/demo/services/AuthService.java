package com.Community.demo.services;

import com.Community.demo.model.User;
import com.Community.demo.controller.AuthController.AuthResponse;

public interface AuthService {
    // register with name,email,password
    User register(String name, String email, String password);

    // login returns token + user (AuthResponse)
    AuthResponse login(String email, String password);

    // get current user from header (or null)
    User getCurrentUser(String authHeader);

    // update profile using supplied user object; returns updated user
    User updateProfile(String authHeader, User userUpdates);

    // change password: returns true if changed
    boolean changePassword(String authHeader, String currentPassword, String newPassword);

    // logout (optional — no-op if using stateless JWT)
    void logout(String authHeader);
}
