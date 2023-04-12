package ru.ncti.modulebackend.controller;

import javassist.NotFoundException;
import lombok.extern.log4j.Log4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.ncti.modulebackend.Email;
import ru.ncti.modulebackend.dto.NewsDTO;
import ru.ncti.modulebackend.dto.StudentDTO;
import ru.ncti.modulebackend.dto.SubjectDTO;
import ru.ncti.modulebackend.dto.TeacherDTO;
import ru.ncti.modulebackend.impl.EmailServiceImpl;
import ru.ncti.modulebackend.service.AdminService;

import java.util.Map;

@RestController
@Log4j
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final EmailServiceImpl emailService;

    public AdminController(AdminService adminService, EmailServiceImpl emailService) {
        this.adminService = adminService;
        this.emailService = emailService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInfoById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(adminService.getInfoById(id));
    }

    @PostMapping("/create-student")
    public ResponseEntity<?> createStudent(@RequestBody StudentDTO dto) {
        try {
            return ResponseEntity.ok(adminService.createStudent(dto));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body("Error");
        }
    }


    @PostMapping("/create-teacher")
    public ResponseEntity<?> createTeacher(@RequestBody TeacherDTO dto) {
        try {
            return ResponseEntity.ok(adminService.createTeacher(dto));
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body("Error");
        }
    }

    @PostMapping("/create-subject")
    public ResponseEntity<?> createSubject(@RequestBody SubjectDTO dto) {
        return ResponseEntity.ok(adminService.createSubject(dto));
    }

    @PostMapping("/add-news")
    public ResponseEntity<?> addNews(@RequestBody NewsDTO dto) {
        return ResponseEntity.ok(adminService.createNews(dto));
    }

    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(adminService.getTeachers());
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file")MultipartFile file) {
        String fileName = file.getOriginalFilename();
        return ResponseEntity.ok(fileName);
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage() {
        return ResponseEntity.ok(emailService.sendSimpleMail(
                new Email("addres@gmail.com", "Notification message",
                        Map.of("username", "username", "password", "password"))));
    }

}
