package com.EdumentumBackend.EdumentumBackend.controller.guest;

import com.EdumentumBackend.EdumentumBackend.dtos.RoleRequest;
import com.EdumentumBackend.EdumentumBackend.dtos.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetailsService;
import com.EdumentumBackend.EdumentumBackend.jwt.JwtService;
import com.EdumentumBackend.EdumentumBackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/guest")
public class GuestController {

    private final UserService userService;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    public GuestController(UserService userService, JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/set-user-role")
    public ResponseEntity<?> setUserRole(
            @Valid @RequestBody RoleRequest roleRequest,
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of(
                    "status", "error",
                    "error", "Missing or invalid Authorization header"));
        }

        String token = authHeader.substring(7);
        Long userId;

        try {
            userId = jwtService.extractUserId(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of(
                    "status", "error",
                    "error", "Invalid or expired token"
            ));
        }

        userService.setUserRole(userId, roleRequest.getRoleName());
        UserResponseDto userResponseDto = userService.findById(userId);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userResponseDto.getEmail());
        return getResponseEntity(userResponseDto, userDetails, jwtService);
    }

    public static ResponseEntity<?> getResponseEntity(UserResponseDto userResponseDto, UserDetails userDetails, JwtService jwtService) {
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
    }
}
