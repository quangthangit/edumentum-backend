package com.EdumentumBackend.EdumentumBackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete( sql = "UPDATE users SET deleted = true WHERE user_id = ?")
@Where(clause = "deleted = false")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull
    @Size(max = 20)
    @Column(nullable = false)
    private String username;

    @NotBlank
    @Size(min = 6)
    @Column(nullable = false)
    private String password;

    @Column
    private String googleId;

    @Column
    private String facebookId;

    @Column(nullable = false)
    private Boolean isActive = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles;

    private boolean deleted = false;

    @PreRemove
    private void preRemove() {
        this.roles.clear();
    }
}
