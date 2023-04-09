package ru.ncti.modulebackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.ncti.modulebackend.Email;
import ru.ncti.modulebackend.dto.NewsDTO;
import ru.ncti.modulebackend.dto.StudentDTO;
import ru.ncti.modulebackend.dto.TeacherDTO;
import ru.ncti.modulebackend.impl.EmailServiceImpl;
import ru.ncti.modulebackend.service.AdminService;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final EmailServiceImpl emailService;

    public AdminController(AdminService adminService, EmailServiceImpl emailService) {
        this.adminService = adminService;
        this.emailService = emailService;
    }

    @PostMapping("/create-student")
    public ResponseEntity<?> createStudent(@RequestBody StudentDTO dto) {
        try {
            return ResponseEntity.ok(adminService.createStudent(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/create-teacher")
    public ResponseEntity<?> createTeacher(@RequestBody TeacherDTO dto) {
        return ResponseEntity.ok(adminService.createTeacher(dto));
    }

    @PostMapping("/add-news")
    public ResponseEntity<?> addNews(@RequestBody NewsDTO dto) {
        return ResponseEntity.ok(adminService.createNews(dto));
    }

    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(adminService.getTeachers());
    }


    @PostMapping("/send")
    public ResponseEntity<?> sendMessage() {
        return ResponseEntity.ok(emailService.sendSimpleMail(
                new Email("addres@gmail.com", "Notification message",
                        Map.of("username", "username", "password", "password"))));
    }

}
