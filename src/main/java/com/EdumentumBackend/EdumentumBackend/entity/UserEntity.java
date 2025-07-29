package com.EdumentumBackend.EdumentumBackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "user_table")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must be at most 100 characters long")
    @Column(nullable = false, unique = true)
    private String gmail;

    @NotNull(message = "Username is required")
    @Size(min = 20, message = "Username must be at least 20 characters long")
    @Column(nullable = false)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Column(nullable = false)
    private String password;

    @Column
    private String googleId;
    
    @Column
    private String facebookId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(nullable = false)
    private Boolean isActive = true;
}
