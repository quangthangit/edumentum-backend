package com.EdumentumBackend.EdumentumBackend.dtos;

import com.EdumentumBackend.EdumentumBackend.entity.Role;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long userId;
    private String gmail;
    private Role role;
    private Boolean isActive;
}
