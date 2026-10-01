package com.kristalball.assetmanagement.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kristalball.assetmanagement.entity.User;
import com.kristalball.assetmanagement.service.AuthService;
import com.kristalball.assetmanagement.service.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(
            AuthService authService,
            JwtService jwtService) {

        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        System.out.println(
                "LOGIN REQUEST: " + request.getUsername()
        );

        User user = authService.authenticate(
                request.getUsername(),
                request.getPassword()
        );

        if (user == null) {

            System.out.println(
                    "LOGIN FAILED: " + request.getUsername()
            );

            return ResponseEntity
                    .status(401)
                    .body("Invalid username or password");
        }

        String role = null;

        if (user.getRole() != null) {
            role = user.getRole().getName();
        }

        String token = jwtService.generateToken(
                user.getUsername(),
                role
        );

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Login successful");
        response.put("token", token);
        response.put("userId", user.getId());
        response.put("fullName", user.getFullName());
        response.put("username", user.getUsername());
        response.put("role", role);

        response.put(
                "baseId",
                user.getBase() == null
                        ? null
                        : user.getBase().getId()
        );

        response.put(
                "baseName",
                user.getBase() == null
                        ? null
                        : user.getBase().getName()
        );

        System.out.println(
                "LOGIN SUCCESS: "
                        + user.getUsername()
                        + " | Role: "
                        + role
        );

        return ResponseEntity.ok(response);
    }

    public static class LoginRequest {

        private String username;
        private String password;

        public LoginRequest() {
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}