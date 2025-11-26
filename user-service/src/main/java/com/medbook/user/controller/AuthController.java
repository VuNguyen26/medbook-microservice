package com.medbook.user.controller;

import com.medbook.user.model.Role;
import com.medbook.user.model.User;
import com.medbook.user.repository.UserRepository;
import com.medbook.user.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * AuthController - xử lý đăng ký, đăng nhập, xác thực JWT, và đồng bộ OAuth2
 * Gateway gọi /auth/oauth2/sync sau khi user đăng nhập thành công qua Google/Facebook
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // =================== REGISTER ===================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        log.info("Register request: {}", user.getEmail());

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email đã tồn tại!"));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole(Role.PATIENT);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Đăng ký thành công!",
                        "email", user.getEmail(),
                        "role", user.getRole().name(),
                        "id", user.getId()
                ));
    }

    // =================== LOGIN ===================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        log.info("Login request: {}", loginRequest.getEmail());

        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Sai email hoặc mật khẩu!"));
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Sai email hoặc mật khẩu!"));
        }

        // Sinh JWT
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "id", user.getId(),
                "email", user.getEmail(),
                "name", user.getName(),
                "role", user.getRole().name(),
                "message", "Đăng nhập thành công!"
        ));
    }

    // =================== CHECK TOKEN ===================
    @GetMapping("/check")
    public ResponseEntity<?> checkToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Thiếu token xác thực!"));
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Token không hợp lệ!"));
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User không tồn tại!"));
        }

        User user = userOpt.get();

        return ResponseEntity.ok(Map.of(
                "email", user.getEmail(),
                "role", role,
                "id", user.getId(),
                "valid", true
        ));
    }

    // =================== OAUTH2 SYNC (Gateway gọi nội bộ) ===================
    /**
     * Được Gateway gọi sau khi OAuth2 login thành công.
     * Payload: { "email": "...", "name": "..." }
     * Trả về: JSON chứa token + user info + id
     */
    @PostMapping("/oauth2/sync")
    public ResponseEntity<?> oauth2Sync(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String name = payload.get("name");

        log.info("OAuth2 sync: {}", email);

        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Thiếu email từ OAuth2 Provider"));
        }

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name != null ? name : "Người dùng");
                    newUser.setPassword(passwordEncoder.encode("OAUTH2_LOGIN"));
                    newUser.setRole(Role.PATIENT);
                    log.info("Tạo mới user OAuth2: {}", email);
                    return userRepository.save(newUser);
                });

        // Sinh JWT đúng ROLE_
        String role = "ROLE_" + user.getRole().name();
        String token = jwtUtil.generateToken(user.getEmail(), role);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "id", user.getId(),
                "email", user.getEmail(),
                "name", user.getName(),
                "role", role // phải là ROLE_PATIENT
        ));
    }

}
