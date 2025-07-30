package com.EdumentumBackend.EdumentumBackend.controller.guest;

import com.EdumentumBackend.EdumentumBackend.dtos.RoleRequest;
import com.EdumentumBackend.EdumentumBackend.dtos.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetailsService;
import com.EdumentumBackend.EdumentumBackend.jwt.JwtService;
import com.EdumentumBackend.EdumentumBackend.service.GoogleTokenVerifierService;
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
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    public GuestController(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
        this.userService = userService;
        this.authManager = authenticationManager;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/set-user-role")
    public ResponseEntity<?> setUserRole(
            @Valid @RequestBody RoleRequest roleRequest,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String email = jwtService.extractUsername(token);
        System.out.println(email);
        userService.setUserRole(email, roleRequest.getRoleName());
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserResponseDto userResponseDto = userService.findByEmail(email);
        String accessToken = jwtService.generateToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(authentication);
        System.out.println(accessToken);
        System.out.println(refreshToken);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", userResponseDto,
                "accessToken", accessToken,
                "refreshToken", refreshToken
        ));
    }

}
