package com.EdumentumBackend.EdumentumBackend.controller.auth;

import com.EdumentumBackend.EdumentumBackend.dtos.UserRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.exception.AuthenticationFailedException;
import com.EdumentumBackend.EdumentumBackend.jwt.JwtService;
import com.EdumentumBackend.EdumentumBackend.service.UserService;
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

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

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
            UserResponseDto user = userService.getByUsername(userRequestDto.getGmail());

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
}
