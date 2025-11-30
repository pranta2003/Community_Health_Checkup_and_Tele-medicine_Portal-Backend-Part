package com.Community.demo.services.impl;

import com.Community.demo.controller.AuthController.AuthResponse;
import com.Community.demo.model.User;
import com.Community.demo.repository.UserRepository;
import com.Community.demo.security.JwtProvider;
import com.Community.demo.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    /**
     * Interface-required 3-arg register method.
     * If frontend doesn't supply a role, register with default ROLE_USER.
     */
    @Override
    public User register(String name, String email, String password) {
        return register(name, email, password, null);
    }

    /**
     * 4-arg register used when frontend provides a role (optional).
     */
    public User register(String name, String email, String password, String roleOpt) {
        String normEmail = email == null ? null : email.trim().toLowerCase(Locale.ROOT);
        if (normEmail == null || normEmail.isEmpty()) throw new RuntimeException("Email is required");
        if (userRepository.existsByEmail(normEmail)) throw new RuntimeException("Email already in use: " + normEmail);

        // default role
        Set<String> roles = Set.of("ROLE_USER");

        // if a role is provided and allowed, set it
        if (roleOpt != null) {
            String r = roleOpt.trim().toUpperCase(Locale.ROOT);
            if (Set.of("ROLE_USER","ROLE_DOCTOR","ROLE_ADMIN","ROLE_PATIENT").contains(r)) {
                roles = Set.of(r);
            }
        }

        User u = User.builder()
                .name(name)
                .email(normEmail)
                .password(passwordEncoder.encode(password))
                .roles(roles)
                .build();
        return userRepository.save(u);
    }

    @Override
    public AuthResponse login(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Invalid credentials"));
        String token = jwtProvider.generateToken(user.getId(), user.getEmail(), user.getRoles()); // include roles
        return new AuthResponse(token, user);
    }

    @Override
    public User getCurrentUser(String authHeader) {
        if (authHeader == null) return null;
        if (!authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7);
        String uid = jwtProvider.getUserIdFromToken(token);
        if (uid == null) return null;
        try {
            Long id = Long.parseLong(uid);
            return userRepository.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public User updateProfile(String authHeader, User userUpdates) {
        User current = getCurrentUser(authHeader);
        if (current == null) throw new RuntimeException("Not authenticated");
        if (userUpdates.getName() != null) current.setName(userUpdates.getName());
        if (userUpdates.getPhone() != null) current.setPhone(userUpdates.getPhone());
        if (userUpdates.getAddress() != null) current.setAddress(userUpdates.getAddress());
        return userRepository.save(current);
    }

    @Override
    public boolean changePassword(String authHeader, String currentPassword, String newPassword) {
        User current = getCurrentUser(authHeader);
        if (current == null) throw new RuntimeException("Not authenticated");
        if (!passwordEncoder.matches(currentPassword, current.getPassword())) {
            return false;
        }
        current.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(current);
        return true;
    }

    @Override
    public void logout(String authHeader) {
        log.info("Logout called (stateless JWT) — no action taken");
    }
}
