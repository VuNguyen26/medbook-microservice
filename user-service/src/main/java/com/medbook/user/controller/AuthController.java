package com.medbook.user.controller;

import com.medbook.user.model.Role;
import com.medbook.user.model.User;
import com.medbook.user.repository.UserRepository;
import com.medbook.user.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // =================== REGISTER ===================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Email already exists!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null) {
            user.setRole(Role.PATIENT);
        }

        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =================== LOGIN ===================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        }

        // tạo JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());
        response.put("message", "Login successful");

        return ResponseEntity.ok(response);
    }

    // =================== CHECK TOKEN (optional) ===================
    @GetMapping("/check")
    public ResponseEntity<?> checkToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Missing token"));
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid token"));
        }

        return ResponseEntity.ok(Map.of("email", email, "role", role, "valid", true));
    }
}
