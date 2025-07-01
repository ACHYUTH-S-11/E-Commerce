package com.platform.controller;

import com.platform.dto.AuthRequest;
import com.platform.dto.AuthResponse;
import com.platform.dto.LoginRequest;
import com.platform.dto.UserDTO;
import com.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        return userService.loginAndReturnUser(request.getUsername());
    }
    
    @PostMapping("/register")
    public String register(@RequestBody AuthRequest request) {
        return userService.register(request);
    }

    @GetMapping("/profile/{username}")
    public UserDTO getProfile(@PathVariable String username) {
        return userService.getProfile(username);
    }

    @PutMapping("/profile/{username}")
    public UserDTO updateProfile(@PathVariable String username, @RequestBody UserDTO newData) {
        return userService.updateProfile(username, newData);
    }
}
