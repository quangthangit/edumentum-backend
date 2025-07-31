package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.UserRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.UserResponseDto;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequestDto);
    UserResponseDto findByEmail(String email);
    void setUserRole(Long userId, String roleName);
    void deleteById(Long id);
    UserResponseDto findById(Long userId);
}
