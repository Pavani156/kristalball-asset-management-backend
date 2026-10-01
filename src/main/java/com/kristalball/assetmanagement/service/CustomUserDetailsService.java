package com.kristalball.assetmanagement.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kristalball.assetmanagement.entity.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userService.getUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(
                    "User not found: " + username);
        }

        if (user.getRole() == null
                || user.getRole().getName() == null
                || user.getRole().getName().isBlank()) {

            throw new UsernameNotFoundException(
                    "User has no role assigned: " + username);
        }

        String role = user.getRole()
                .getName()
                .trim()
                .toUpperCase();

        if (role.startsWith("ROLE_")) {
            role = role.substring(5);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(
                        new SimpleGrantedAuthority(
                                "ROLE_" + role))
                .build();
    }
}