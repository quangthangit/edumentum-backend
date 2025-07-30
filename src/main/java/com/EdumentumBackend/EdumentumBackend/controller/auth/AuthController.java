package com.EdumentumBackend.EdumentumBackend.controller.auth;

import com.EdumentumBackend.EdumentumBackend.dtos.RoleRequest;
import com.EdumentumBackend.EdumentumBackend.dtos.UserRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.UserRequestLoginDto;
import com.EdumentumBackend.EdumentumBackend.dtos.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.exception.AuthenticationFailedException;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetailsService;
import com.EdumentumBackend.EdumentumBackend.jwt.JwtService;
import com.EdumentumBackend.EdumentumBackend.service.GoogleTokenVerifierService;
import com.EdumentumBackend.EdumentumBackend.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final GoogleTokenVerifierService googleTokenVerifierService;

    public AuthController(AuthenticationManager authManager, JwtService jwtService, UserService userService, CustomUserDetailsService customUserDetailsService, GoogleTokenVerifierService googleTokenVerifierService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.googleTokenVerifierService = googleTokenVerifierService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserRequestLoginDto userRequestLoginDto) {
        try {
            Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(userRequestLoginDto.getEmail(), userRequestLoginDto.getPassword()));

            String token = jwtService.generateToken(authentication);
            String tokenRefresh = jwtService.generateRefreshToken(authentication);
            UserResponseDto user = userService.findByEmail(userRequestLoginDto.getEmail());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "User login successfully",
                    "data", Map.of("user", user,
                            "accessToken", token,
                            "refreshToken", tokenRefresh)));

        } catch (BadCredentialsException ex) {
            throw new AuthenticationFailedException("Invalid username or password");
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred during login");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto createdUser = userService.createUser(userRequestDto);
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(userRequestDto.getEmail(), userRequestDto.getPassword()));
        String token = jwtService.generateToken(authentication);
        String tokenRefresh = jwtService.generateRefreshToken(authentication);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "success",
                "message", "User registered successfully",
                "data", Map.of("user", createdUser,
                        "accessToken", token,
                        "refreshToken", tokenRefresh)));
    }

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Missing Google token"));
        }

        try {
            GoogleIdToken.Payload payload = googleTokenVerifierService.verifyToken(token);
            String email = payload.getEmail();
            String username = (String) payload.get("name");

            UserResponseDto userResponseDto;
            try {
                userResponseDto = userService.findByEmail(email);
            } catch (NotFoundException e) {
                UserRequestDto requestDto = new UserRequestDto();
                requestDto.setEmail(email);
                requestDto.setPassword(UUID.randomUUID().toString());
                requestDto.setUsername(username);
                userResponseDto = userService.createUser(requestDto);
            }

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = jwtService.generateToken(authentication);
            String refreshToken = jwtService.generateRefreshToken(authentication);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Login with Google successful",
                    "data", Map.of("user", userResponseDto,
                            "accessToken", accessToken,
                            "refreshToken", refreshToken)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "error",
                    "message", "Invalid Google ID token"
            ));
        }
    }

}
