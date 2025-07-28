package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.UserRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.UserResponseDto;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequestDto);
    UserResponseDto getByUsername(String gmail);
}
