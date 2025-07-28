package com.EdumentumBackend.EdumentumBackend.jwt;

import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String gmail) {
        UserEntity user = userRepository.findByGmail(gmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));

        return new org.springframework.security.core.userdetails.User(
                user.getGmail(),
                user.getPassword(),
                authorities
        );
    }
}