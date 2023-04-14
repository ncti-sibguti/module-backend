package ru.ncti.modulebackend.controller;

import com.opencsv.exceptions.CsvException;
import javassist.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.ncti.modulebackend.dto.NewsDTO;
import ru.ncti.modulebackend.dto.StudentDTO;
import ru.ncti.modulebackend.dto.SubjectDTO;
import ru.ncti.modulebackend.dto.TeacherDTO;
import ru.ncti.modulebackend.service.AdminService;

import java.io.IOException;

@RestController
@Log4j2
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInfoById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(adminService.getInfoById(id));
    }

    @PostMapping("/create-student")
    public ResponseEntity<?> createStudent(@RequestBody StudentDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.createStudent(dto));
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PostMapping("/create-teacher")
    public ResponseEntity<?> createTeacher(@RequestBody TeacherDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.createTeacher(dto));
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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

    @GetMapping("/teachers")
    public ResponseEntity<?> getTeacher() {
        return ResponseEntity.ok(adminService.getTeachers());
    }

    @GetMapping("/students")
    public ResponseEntity<?> getStudents() {
        return ResponseEntity.ok(adminService.getStudents());
    }

    @GetMapping("/groups")
    public ResponseEntity<?> getGroups() {
        return ResponseEntity.ok(adminService.getGroups());
    }

    @PostMapping("/upload-students")
    public ResponseEntity<?> uploadStudents(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.uploadStudents(file));
        } catch (IOException | CsvException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/upload-teacher")
    public ResponseEntity<?> uploadTeacher(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.uploadTeacher(file));
        } catch (IOException | CsvException | NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/teacher/{id}")
    public ResponseEntity<?> getTeacherById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.getTeacherById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/student/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.getStudentById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @DeleteMapping("/student/{id}")
    public ResponseEntity<?> deleteStudentById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.deleteStudentById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @DeleteMapping("/teacher/{id}")
    public ResponseEntity<?> deleteTeacherById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.deleteTeacherById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
