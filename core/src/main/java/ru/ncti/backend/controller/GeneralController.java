package ru.ncti.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ncti.backend.service.UserService;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/general")
public class GeneralController {

    private final UserService userService;

    public GeneralController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@PathParam("type") String type) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers(type));
    }

}
