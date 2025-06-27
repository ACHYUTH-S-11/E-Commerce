package com.platform.service;

import com.platform.security.JavaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.platform.dto.UserDTO;
import com.platform.dto.LoginRequest;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.authentication.*;
import com.platform.repository.UserRepository;
import com.platform.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.platform.dto.AuthRequest;
import com.platform.dto.AuthResponse;

import  org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JavaUtil jwtUtil;
    @Lazy // Lazy initialization to avoid circular dependency issues
    @Autowired
    private AuthenticationManager authenticationManager;

    public String register(AuthRequest request) {
        if(userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("User already exists");
        }
        
        User user = new User(
            request.getUsername(),
            passwordEncoder.encode(request.getPassword()),
            request.getEmail(),
            request.getFirstName(),
            request.getLastName()
        );
        userRepository.save(user);
        return "User registered successfully";
    }

    public String login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));
        return jwtUtil.generateToken(user);
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
        userRepository.save(user);
        return newData;
    }

    public AuthResponse loginAndReturnUser(AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }
}