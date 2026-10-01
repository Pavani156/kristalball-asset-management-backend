package com.kristalball.assetmanagement.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kristalball.assetmanagement.entity.User;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserService userService,
            PasswordEncoder passwordEncoder) {

        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public User authenticate(String username, String password) {

        User user = userService.getUserByUsername(username);

        if (user == null) {
            return null;
        }

        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {
            return null;
        }

        return user;
    }
}