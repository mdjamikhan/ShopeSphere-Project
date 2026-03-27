package com.lpu.shopsphere.auth.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lpu.shopsphere.auth.domain.User;
import com.lpu.shopsphere.auth.domain.UserRole;
import com.lpu.shopsphere.auth.dto.AuthResponse;
import com.lpu.shopsphere.auth.dto.LoginRequest;
import com.lpu.shopsphere.auth.dto.SignupRequest;
import com.lpu.shopsphere.auth.repo.UserRepository;
import com.lpu.shopsphere.auth.security.JwtUtil;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/auth")
public class AuthController {
  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;
  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public AuthController(UserRepository userRepository, JwtUtil jwtUtil) {
    this.userRepository = userRepository;
    this.jwtUtil = jwtUtil;
  }

  @PostMapping({ "/signup", "/auth/signup", "/auth/auth/signup" })
  public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
    }

    User user = new User();
    user.setName(request.getName());
    user.setEmail(request.getEmail().toLowerCase());
    user.setPassword(passwordEncoder.encode(request.getPassword()));

    UserRole role = UserRole.ROLE_CUSTOMER;
    if (request.getRole() != null && !request.getRole().isBlank()) {
      try {
        role = UserRole.valueOf(request.getRole().trim());
      } catch (Exception ignored) {
        role = UserRole.ROLE_CUSTOMER;
      }
    }
    user.setRole(role);

    userRepository.save(user);
    return ResponseEntity.ok(Map.of("message", "User registered successfully"));
  }

  @PostMapping({ "/login", "/auth/login", "/auth/auth/login" })
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
    String email = request.getUsername().toLowerCase();
    User user = userRepository.findByEmail(email).orElse(null);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
    return ResponseEntity.ok(new AuthResponse(token));
  }
}

