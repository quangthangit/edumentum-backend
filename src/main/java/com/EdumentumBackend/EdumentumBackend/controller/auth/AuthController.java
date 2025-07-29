package com.EdumentumBackend.EdumentumBackend.controller.auth;

import com.EdumentumBackend.EdumentumBackend.dtos.UserRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.exception.AuthenticationFailedException;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.jwt.JwtService;
import com.EdumentumBackend.EdumentumBackend.service.GoogleTokenVerifierService;
import com.EdumentumBackend.EdumentumBackend.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    private final GoogleTokenVerifierService googleTokenVerifierService;

    public AuthController(GoogleTokenVerifierService googleTokenVerifierService) {
        this.googleTokenVerifierService = googleTokenVerifierService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserRequestDto userRequestDto) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userRequestDto.getGmail(),
                            userRequestDto.getPassword()
                    )
            );

            String token = jwtService.generateToken(authentication);
            String tokenRefresh = jwtService.generateRefreshToken(authentication);
            UserResponseDto user = userService.getByGmail(userRequestDto.getGmail());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "User login successfully",
                    "data", Map.of(
                            "user", user,
                            "accessToken", token,
                            "refreshToken", tokenRefresh
                    )
            ));

        } catch (BadCredentialsException ex) {
            throw new AuthenticationFailedException("Invalid username or password");
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred during login");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto createdUser = userService.createUser(userRequestDto);
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userRequestDto.getGmail(),
                        userRequestDto.getPassword()
                )
        );
        String token = jwtService.generateToken(authentication);
        String tokenRefresh = jwtService.generateRefreshToken(authentication);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "success",
                "message", "User registered successfully",
                "data", Map.of(
                        "user", createdUser,
                        "accessToken", token,
                        "refreshToken", tokenRefresh
                )
        ));
    }

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> request) {
        String token = request.get("token");

        try {
            GoogleIdToken.Payload payload = googleTokenVerifierService.verifyToken(token);
            String email = payload.getEmail();
            String username = (String) payload.get("name");
            UserResponseDto userResponseDto;
            try {
                userResponseDto = userService.getByGmail(email);
            } catch (NotFoundException e) {
                UserRequestDto requestDto = new UserRequestDto();
                requestDto.setGmail(email);
                requestDto.setPassword(email);
                requestDto.setUsername(username);
                userResponseDto = userService.createUser(requestDto);
            }

            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            email
                    )
            );

            String accessToken = jwtService.generateToken(authentication);
            String refreshToken = jwtService.generateRefreshToken(authentication);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Login with Google successful",
                    "data", Map.of(
                            "user", userResponseDto,
                            "accessToken", accessToken,
                            "refreshToken", refreshToken
                    )
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "error",
                    "message", "Invalid ID token"
            ));
        }
    }
}
