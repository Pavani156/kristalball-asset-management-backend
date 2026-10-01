package com.kristalball.assetmanagement.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kristalball.assetmanagement.service.CustomUserDetailsService;
import com.kristalball.assetmanagement.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        // No Authorization header
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        try {

            String token = authorizationHeader.substring(7);

            // Extract username from JWT
            String username = jwtService.extractUsername(token);

            if (username != null
                    && SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {

                // Load user from database
                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(username);

                // Validate token
                if (jwtService.isTokenValid(token)) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request));

                    // Put authenticated user into Spring Security
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                }
            }

        } catch (Exception e) {

            // Invalid token -> continue without authentication
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}