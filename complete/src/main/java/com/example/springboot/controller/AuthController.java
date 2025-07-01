package com.example.springboot.controller;

import com.example.springboot.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private TokenService tokenService;

    @PostMapping("/token")
    public ResponseEntity<?> getToken(@RequestParam String username, @RequestParam String role) {
        String token = tokenService.generateToken(username, role);
        return ResponseEntity.ok().body("Bearer " + token);
    }
}
