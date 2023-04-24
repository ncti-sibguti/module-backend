package ru.ncti.modulebackend.controller;

import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ncti.modulebackend.service.TeacherService;

@RestController
@RequestMapping("/teacher")
@PreAuthorize(value = "hasRole('TEACHER')")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping()
    public ResponseEntity<?> getInfo() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(teacherService.getInfo());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/schedule")
    public ResponseEntity<?> getSchedule() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(teacherService.getSchedule());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
