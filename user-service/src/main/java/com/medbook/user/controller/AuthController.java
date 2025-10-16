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
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // =================== REGISTER ===================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email already exists!"));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole(Role.PATIENT);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "User registered successfully",
                        "email", user.getEmail(),
                        "role", user.getRole().name()
                ));
    }

    // =================== LOGIN ===================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isEmpty()) {
            System.out.println("Không tìm thấy email: " + loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        }

        User user = userOpt.get();

        // Thêm debug
        System.out.println("Email tìm thấy: " + user.getEmail());
        System.out.println("Password nhập vào: " + loginRequest.getPassword());
        System.out.println("Password trong DB: " + user.getPassword());
        System.out.println("Kết quả so khớp: " + passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "email", user.getEmail(),
                "role", user.getRole().name(),
                "message", "Login successful"
        ));
    }


    // =================== CHECK TOKEN ===================
    @GetMapping("/check")
    public ResponseEntity<?> checkToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Missing token"));
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid token"));
        }

        return ResponseEntity.ok(Map.of("email", email, "role", role, "valid", true));
    }
}
