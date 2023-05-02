package ru.ncti.backend.controller;

import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ncti.backend.service.StudentService;

@RestController
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getInfo() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(studentService.getInfo());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<?> getSchedule() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(studentService.getSchedule());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

//    part 2
//    @GetMapping("/certificate")
//    public ResponseEntity<?> getCertificates() {
//        return ResponseEntity.status(HttpStatus.OK).body(studentService.getCertificates());
//    }
//
//    @PostMapping("/certificate/{id}")
//    public ResponseEntity<?> getCertificate(@PathVariable("id") Long id) {
//        return ResponseEntity.status(HttpStatus.OK).body(studentService.getCertificate(id));
//    }
}
