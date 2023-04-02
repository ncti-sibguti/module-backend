package ru.ncti.modulebackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ncti.modulebackend.dto.UserDTO;
import ru.ncti.modulebackend.security.AuthDTO;
import ru.ncti.modulebackend.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO dto) {
        try {
            return ResponseEntity.ok(authService.register(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

}
