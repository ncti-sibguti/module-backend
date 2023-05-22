package ru.ncti.backend.controller;

import com.opencsv.exceptions.CsvException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.ncti.backend.dto.AdminDTO;
import ru.ncti.backend.dto.GroupDTO;
import ru.ncti.backend.dto.ResetPasswordDTO;
import ru.ncti.backend.dto.SampleDTO;
import ru.ncti.backend.dto.StudentDTO;
import ru.ncti.backend.dto.SubjectDTO;
import ru.ncti.backend.dto.TeacherDTO;
import ru.ncti.backend.service.AdminService;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@Log4j2
@RequestMapping("/admin")
public class AdminController {

    private final static String TEACHERS_URL = "/teachers";
    private final static String STUDENTS_URL = "/students";
    private final static String GROUPS_URL = "/groups";
    private final static String SUBJECT_URL = "/subjects";

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInfoById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(adminService.getInfoById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePasswordForAdminById(@PathVariable("id") Long id, @RequestBody AdminDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.updatePasswordForAdminById(id, dto));
    }


    @GetMapping(TEACHERS_URL)
    public ResponseEntity<?> getTeacher() {
        return ResponseEntity.ok(adminService.getTeachers());
    }

    @GetMapping(TEACHERS_URL + "/{id}")
    public ResponseEntity<?> getTeacherById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.getTeacherById(id));
    }

    @GetMapping(STUDENTS_URL)
    public ResponseEntity<?> getStudents(@RequestParam("group") Long group) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.getStudents(group));
    }

    @GetMapping(STUDENTS_URL + "/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.getStudentById(id));
    }

    @GetMapping(GROUPS_URL)
    public ResponseEntity<?> getGroups() {
        return ResponseEntity.ok(adminService.getGroups());
    }


    @GetMapping(GROUPS_URL + "/{id}")
    public ResponseEntity<?> getGroupById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.getGroupById(id));
    }

    @GetMapping(SUBJECT_URL)
    public ResponseEntity<?> getSubjecst() {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.getSubjects());
    }

    @PostMapping(STUDENTS_URL)
    public ResponseEntity<?> createStudent(@Valid @RequestBody StudentDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.createStudent(dto));
    }

    @PostMapping(TEACHERS_URL)
    public ResponseEntity<?> createTeacher(@Valid @RequestBody TeacherDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.createTeacher(dto));
    }


    @PostMapping("/schedule")
    public ResponseEntity<?> createSchedule(@RequestBody SampleDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.createSchedule(dto));
    }


    @PostMapping(GROUPS_URL)
    public ResponseEntity<?> createGroup(@RequestBody GroupDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.addGroup(dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

    @PostMapping(SUBJECT_URL)
    public ResponseEntity<?> addSubject(@RequestBody SubjectDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.addSubject(dto));
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

    @PostMapping("/upload-teachers")
    public ResponseEntity<?> uploadTeacher(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.uploadTeacher(file));
        } catch (IOException | CsvException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/upload-schedule")
    public ResponseEntity<?> uploadSchedule(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.uploadSchedule(file));
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.resetPasswordForUserById(dto));
    }

    @DeleteMapping(TEACHERS_URL + "/{id}")
    public ResponseEntity<?> deleteTeacherById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.deleteTeacherById(id));
    }

    @DeleteMapping(STUDENTS_URL + "/{id}")
    public ResponseEntity<?> deleteStudentById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.deleteStudentById(id));
    }

    @DeleteMapping(GROUPS_URL + "/{id}")
    public ResponseEntity<?> deleteGroupById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.deleteGroupById(id));
    }

}
