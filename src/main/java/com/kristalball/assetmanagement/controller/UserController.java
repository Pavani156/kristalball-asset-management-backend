package com.kristalball.assetmanagement.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kristalball.assetmanagement.entity.User;
import com.kristalball.assetmanagement.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {

        User user = userService.getUserById(id);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> createUser(
            @RequestBody User user) {

        User savedUser = userService.saveUser(user);

        return ResponseEntity.status(201).body(savedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id) {

        User existingUser = userService.getUserById(id);

        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}