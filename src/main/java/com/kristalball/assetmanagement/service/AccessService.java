package com.kristalball.assetmanagement.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.kristalball.assetmanagement.entity.User;

@Service
public class AccessService {

    private final UserService userService;

    public AccessService(UserService userService) {
        this.userService = userService;
    }

    public User currentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        return userService.getUserByUsername(authentication.getName());
    }

    private String roleName() {

        User user = currentUser();

        if (user == null || user.getRole() == null
                || user.getRole().getName() == null) {
            return "";
        }

        return user.getRole().getName()
                .trim()
                .toUpperCase()
                .replace(" ", "_")
                .replace("ROLE_", "");
    }

    public boolean isAdmin() {
        return "ADMIN".equals(roleName());
    }

    public boolean isCommander() {
        return "BASE_COMMANDER".equals(roleName());
    }

    public boolean isLogistics() {
        return "LOGISTICS_OFFICER".equals(roleName());
    }

    public Long assignedBaseId() {

        User user = currentUser();

        if (user == null || user.getBase() == null) {
            return null;
        }

        return user.getBase().getId();
    }

    public void requireBase(Long baseId) {

        // Admin and Logistics Officer can access all bases
        if (isAdmin() || isLogistics()) {
            return;
        }

        // Base Commander can access only their assigned base
        if (isCommander()) {

            Long assignedBase = assignedBaseId();

            if (baseId != null
                    && assignedBase != null
                    && baseId.equals(assignedBase)) {
                return;
            }

            throw new AccessDeniedException(
                    "You are not allowed to access this base");
        }

        throw new AccessDeniedException(
                "You are not allowed to access this base");
    }

    public void requireTransferBases(
            Long fromBaseId,
            Long toBaseId) {

        if (fromBaseId == null || toBaseId == null) {
            throw new AccessDeniedException(
                    "Both source and destination bases are required");
        }

        if (fromBaseId.equals(toBaseId)) {
            throw new AccessDeniedException(
                    "Source and destination bases must be different");
        }

        // Admin and Logistics Officer can transfer between any bases
        if (isAdmin() || isLogistics()) {
            return;
        }

        // Base Commander can transfer if either base is their assigned base
        if (isCommander()) {

            Long assignedBase = assignedBaseId();

            if (assignedBase != null && fromBaseId.equals(assignedBase)) {
                return;
            }
        }

        throw new AccessDeniedException(
                "You are not allowed to operate this transfer");
    }
}