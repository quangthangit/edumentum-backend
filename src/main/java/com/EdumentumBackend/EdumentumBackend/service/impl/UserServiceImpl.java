package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.UserRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.Role;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.exception.AlreadyExistsException;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        Optional<UserEntity> userCheck = userRepository.findByGmail(userRequestDto.getGmail());
        if (userCheck.isPresent()) {
            throw new AlreadyExistsException("User with gmail " + userRequestDto.getGmail() + " already exists");
        }

        UserEntity user = UserEntity.builder()
                .gmail(userRequestDto.getGmail())
                .password(passwordEncoder.encode(userRequestDto.getPassword()))
                .isActive(true)
                .role(Role.ROLE_STUDENT)
                .build();

        UserEntity savedUser = userRepository.save(user);

        return UserResponseDto.builder()
                .userId(savedUser.getUserId())
                .gmail(savedUser.getGmail())
                .isActive(savedUser.getIsActive())
                .role(savedUser.getRole())
                .build();
    }

    @Override
    public UserResponseDto getByUsername(String gmail) {
        UserEntity user = userRepository.findByGmail(gmail)
                .orElseThrow(() -> new NotFoundException("User with gmail " + gmail + " not found"));
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .gmail(user.getGmail())
                .isActive(user.getIsActive())
                .role(user.getRole())
                .build();
    }
}
