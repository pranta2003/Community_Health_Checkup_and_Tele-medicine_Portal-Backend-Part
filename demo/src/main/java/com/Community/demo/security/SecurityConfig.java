package com.Community.demo.security;

import com.Community.demo.model.User;
import com.Community.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserRepository userRepository,
                                                       PasswordEncoder encoder) {
        return authentication -> {
            String email = authentication.getPrincipal().toString();
            String rawPassword = authentication.getCredentials().toString();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BadCredentialsException("Invalid username/password"));
            if (!encoder.matches(rawPassword, user.getPassword())) {
                throw new BadCredentialsException("Invalid username/password");
            }

            Set<SimpleGrantedAuthority> authorities =
                    user.getRoles() == null ? Set.of()
                            : user.getRoles().stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toSet());

            return new UsernamePasswordAuthenticationToken(email, null, authorities);
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(restAuthenticationEntryPoint()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Public/Auth Endpoints
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/**").permitAll()
                        // Public Doctor Lookup
                        .requestMatchers(HttpMethod.GET, "/api/public/doctors").permitAll()
                        // Swagger/Docs
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Specific Public GET Endpoints
                        .requestMatchers(HttpMethod.GET, "/api/notifications/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/appointments/*/join-info").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/consultations/*/join-info").permitAll()
                        // Admin Protected Endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // All other requests MUST be authenticated
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return (HttpServletRequest request, HttpServletResponse response,
                org.springframework.security.core.AuthenticationException authException) -> {
            log.warn("Unauthorized request to {}: {}", request.getRequestURI(), authException.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            String body = String.format(
                    "{\"timestamp\":\"%s\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\",\"path\":\"%s\"}",
                    java.time.Instant.now(), authException.getMessage(), request.getRequestURI()
            );
            try {
                response.getWriter().write(body);
            } catch (IOException e) {
                log.error("Failed to write 401 response body: {}", e.getMessage());
            }
        };
    }

    /**
     * Required by HttpSecurity.cors(...).  Provides CORS rules for Spring Security.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // CRITICAL FIX: Explicitly listing origins (local hosts and common live server ports)
        // This removes the illegal "*" wildcard.
        cfg.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3000", "http://127.0.0.1:5501", "http://localhost:5501"));

        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        cfg.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    /**
     * Optional: MVC-level CORS (must also be fixed).
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins( "http://127.0.0.1:5501", "http://localhost:5501")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}