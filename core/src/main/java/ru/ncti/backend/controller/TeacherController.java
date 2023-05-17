package ru.ncti.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ncti.backend.service.TeacherService;

@RestController
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        return ResponseEntity.status(HttpStatus.OK).body(teacherService.getProfile());
    }

    @GetMapping("/schedule")
    public ResponseEntity<?> getSchedule() {
        return ResponseEntity.status(HttpStatus.OK).body(teacherService.getSchedule());
    }

}
