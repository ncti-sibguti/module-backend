package ru.ncti.modulebackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ncti.modulebackend.dto.NewsDTO;
import ru.ncti.modulebackend.dto.StudentDTO;
import ru.ncti.modulebackend.dto.TeacherDTO;
import ru.ncti.modulebackend.service.AdminService;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
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

}
