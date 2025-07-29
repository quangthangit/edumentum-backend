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

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private RoleService roleService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleServiceImpl roleService) {
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        Optional<UserEntity> userCheck = userRepository.findByGmail(userRequestDto.getGmail());
        if (userCheck.isPresent()) {
            throw new AlreadyExistsException("User with gmail " + userRequestDto.getGmail() + " already exists");
        }
        RoleEntity role = roleService.findByName("ROLE_STUDENT");
        UserEntity user = UserEntity.builder()
                .gmail(userRequestDto.getGmail())
                .password(passwordEncoder.encode(userRequestDto.getPassword()))
                .isActive(true)
                .username(userRequestDto.getUsername())
                .roles(Collections.singleton(role))
                .build();

        UserEntity savedUser = userRepository.save(user);

        return UserResponseDto.builder()
                .userId(savedUser.getUserId())
                .gmail(savedUser.getGmail())
                .isActive(savedUser.getIsActive())
                .username(savedUser.getUsername())
                .roles(savedUser.getRoles())
                .build();
    }

    @Override
    public UserResponseDto findByGmail(String gmail) {
        UserEntity user = userRepository.findByGmail(gmail)
                .orElseThrow(() -> new NotFoundException("User with gmail " + gmail + " not found"));
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .gmail(user.getGmail())
                .username(user.getUsername())
                .isActive(user.getIsActive())
                .roles(user.getRoles())
                .build();
    }
}
