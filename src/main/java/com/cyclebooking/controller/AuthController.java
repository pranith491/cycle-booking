package com.cyclebooking.controller;

import com.cyclebooking.model.AppUser;
import com.cyclebooking.repository.UserRepository;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public AuthController(UserRepository users) { this.users = users; }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String,String> body) {
        String name = clean(body.get("name")), email = clean(body.get("email")).toLowerCase();
        String password = body.getOrDefault("password", "");
        if (name.isBlank() || email.isBlank() || password.length() < 6)
            return ResponseEntity.badRequest().body(Map.of("message","Enter name, email and a password with at least 6 characters."));
        if (users.existsByEmailIgnoreCase(email))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","That email is already registered."));
        AppUser user = users.save(new AppUser(name, email, encoder.encode(password)));
        return ResponseEntity.ok(Map.of("id", user.getId(), "name", user.getName(), "email", user.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
        String email = clean(body.get("email")).toLowerCase();
        String password = body.getOrDefault("password", "");
        Optional<AppUser> found = users.findByEmailIgnoreCase(email);
        if (found.isEmpty() || !encoder.matches(password, found.get().getPasswordHash()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Incorrect email or password."));
        AppUser u = found.get();
        return ResponseEntity.ok(Map.of("id",u.getId(),"name",u.getName(),"email",u.getEmail()));
    }
    private String clean(String s) { return s == null ? "" : s.trim(); }
}
