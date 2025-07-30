package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.UserRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.RoleEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.exception.AlreadyExistsException;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.RoleService;
import com.EdumentumBackend.EdumentumBackend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleServiceImpl roleService) {
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        Optional<UserEntity> userCheck = userRepository.findByEmail(userRequestDto.getEmail());
        if (userCheck.isPresent()) {
            throw new AlreadyExistsException("User with gmail " + userRequestDto.getEmail() + " already exists");
        }
        RoleEntity role = roleService.findByName("ROLE_GUEST");
        UserEntity user = UserEntity.builder()
                .email(userRequestDto.getEmail())
                .password(passwordEncoder.encode(userRequestDto.getPassword()))
                .isActive(true)
                .username(userRequestDto.getUsername())
                .roles(Collections.singleton(role))
                .build();

        UserEntity savedUser = userRepository.save(user);

        return UserResponseDto.builder()
                .userId(savedUser.getUserId())
                .email(savedUser.getEmail())
                .isActive(savedUser.getIsActive())
                .username(savedUser.getUsername())
                .roles(user.getRoles())
                .build();
    }

    @Override
    public UserResponseDto findByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with gmail " + email + " not found"));
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .isActive(user.getIsActive())
                .roles(user.getRoles())
                .build();
    }

    @Override
    public void setUserRole(String email, String roleName) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with Gmail " + email + " not found"));

        if (!roleName.equals("ROLE_STUDENT") && !roleName.equals("ROLE_TEACHER")) {
            throw new IllegalArgumentException("Invalid role: " + roleName);
        }

        RoleEntity newRole = roleService.findByName(roleName);
        Set<RoleEntity> currentRoles = user.getRoles();

        currentRoles.removeIf(role -> role.getName().equals("ROLE_GUEST"));

        currentRoles.clear();
        currentRoles.add(newRole);

        user.setRoles(currentRoles);
        userRepository.save(user);
    }


    private boolean hasOnlyGuestRole(Set<RoleEntity> roles) {
        if (roles == null || roles.size() != 1) return false;
        return roles.iterator().next().getName().equals("ROLE_GUEST");
    }

}
