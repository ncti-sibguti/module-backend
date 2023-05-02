package ru.ncti.backend.controller;

import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ncti.backend.service.UserService;

@RestController
@RequestMapping()
public class GeneralController {

    private final UserService userService;

    public GeneralController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@Param("type") String type) {
        return ResponseEntity.ok(userService.getUsers(type));
    }


}
