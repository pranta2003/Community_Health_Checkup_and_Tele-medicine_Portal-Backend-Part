package com.Community.demo.security;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collection;
import java.util.stream.Collectors;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; // <--- correct import
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JwtFilter: validates Bearer token and populates Spring Security context.
 * Expects JwtProvider bean to provide:
 *   - boolean validateToken(String token)
 *   - String getUserIdFromToken(String token)   (or username)
 *   - Set<String> getRolesFromToken(String token)   <-- returns roles like "ROLE_USER"
 *
 * If your JwtProvider returns List<String> instead of Set<String>, change the type below.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtProvider jwtProvider;

    public JwtFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = resolveToken(request);
            if (token != null && !token.isBlank()) {
                boolean valid = jwtProvider.validateToken(token);
                if (valid) {
                    // prefer user id or username from token
                    String principal = jwtProvider.getUserIdFromToken(token);
                    if (principal == null) {
                        principal = jwtProvider.getUsernameFromToken(token); // try alternate method if you have it
                    }

                    // try to extract roles (Set<String> expected)
                    Set<String> roles = Collections.emptySet();
                    try {
                        roles = jwtProvider.getRolesFromToken(token); // expects Set<String>
                    } catch (NoSuchMethodError e) {
                        // If your JwtProvider uses a different signature (e.g. List<String>),
                        // adapt here. For example, if it returns List<String>:
                        // List<String> list = jwtProvider.getRolesFromToken(token);
                        // roles = new HashSet<>(list);
                        log.debug("getRolesFromToken method not found or incompatible - defaulting to ROLE_USER");
                    } catch (Exception e) {
                        log.debug("Could not extract roles from token: {}", e.getMessage());
                    }

                    if (principal != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        Collection<SimpleGrantedAuthority> authorities =
                                (roles == null || roles.isEmpty())
                                        ? Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                                        : roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(principal, null, authorities);
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);

                        log.debug("JWT authentication set for user: {}", principal);
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Could not set user authentication in security context: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
