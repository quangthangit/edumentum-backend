package com.EdumentumBackend.EdumentumBackend.controller.guest;

import com.EdumentumBackend.EdumentumBackend.dtos.RoleRequest;
import com.EdumentumBackend.EdumentumBackend.dtos.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.jwt.JwtService;
import com.EdumentumBackend.EdumentumBackend.service.GoogleTokenVerifierService;
import com.EdumentumBackend.EdumentumBackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/guest")
public class GuestController {

    private final UserService userService;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public GuestController(UserService userService,AuthenticationManager authenticationManager,JwtService jwtService) {
        this.userService = userService;
        this.authManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/set-user-role")
    public ResponseEntity<?> setUserRole(@Valid @RequestBody RoleRequest roleRequest) {
        userService.setUserRole(roleRequest.getGmail(), roleRequest.getRoleName());
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(roleRequest.getGmail(), roleRequest.getGmail()));
        UserResponseDto userResponseDto = userService.findByGmail(roleRequest.getGmail());
        String accessToken = jwtService.generateToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(authentication);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data",userResponseDto,
                "accessToken", accessToken,
                "refreshToken", refreshToken
        ));
    }
}
