package com.platform.controller;

import com.platform.dto.AuthRequest;
import com.platform.dto.AuthResponse;
import com.platform.dto.UserDTO;
import com.platform.dto.LoginRequest;
import com.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody AuthRequest request) {
        return userService.register(request);
    }
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        String token = userService.login(request);
        return new AuthResponse(token);
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
