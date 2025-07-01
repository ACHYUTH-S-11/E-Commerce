package com.platform.service;

import com.platform.dto.AuthRequest;
import com.platform.dto.AuthResponse;
import com.platform.dto.LoginRequest;
import com.platform.dto.UserDTO;
import com.platform.model.Role;
import com.platform.model.User;
import com.platform.repository.UserRepository;
import com.platform.security.JavaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JavaUtil jwtUtil;

    public String register(AuthRequest request) {
        User user = new User(
            request.getUsername(),
            passwordEncoder.encode(request.getPassword()),
            request.getEmail(),
            request.getFirstName(),
            request.getLastName()
        );
        user.setRoles(Set.of(Role.ROLE_USER));
        userRepository.save(user);
        return "User registered successfully";
    }

    public AuthResponse loginAndReturnUser(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtUtil.generateToken(user);
        AuthResponse response = new AuthResponse(token, user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName());
        response.setRoles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
        return response;
    }

    public UserDTO getProfile(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        return userDTO;
    }

    public UserDTO updateProfile(String username, UserDTO newData) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmail(newData.getEmail());
        user.setFirstName(newData.getFirstName());
        user.setLastName(newData.getLastName());
        userRepository.save(user);
        return newData;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.name()))
            .toList();
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(authorities)
            .build();
    }
}