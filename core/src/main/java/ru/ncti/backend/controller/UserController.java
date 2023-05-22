package ru.ncti.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ncti.backend.dto.ChangePasswordDTO;
import ru.ncti.backend.dto.ResetPasswordDTO;
import ru.ncti.backend.service.UserService;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@PathParam("type") String type) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers(type));
    }

    @PatchMapping("/change-password")
    private ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.changePassword(dto, passwordEncoder));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
