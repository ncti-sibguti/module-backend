package ru.ncti.backend.controller;

import com.opencsv.exceptions.CsvException;
import javassist.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import ru.ncti.backend.dto.ResatPasswordDTO;
import ru.ncti.backend.dto.ScheduleDTO;
import ru.ncti.backend.dto.StudentDTO;
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
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.updatePasswordForAdminById(id, dto));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }


    @GetMapping(TEACHERS_URL)
    public ResponseEntity<?> getTeacher() {
        return ResponseEntity.ok(adminService.getTeachers());
    }

    @GetMapping(TEACHERS_URL + "/{id}")
    public ResponseEntity<?> getTeacherById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.getTeacherById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping(STUDENTS_URL)
    public ResponseEntity<?> getStudents(@RequestParam("group") Long group) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.getStudents(group));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

    @GetMapping(STUDENTS_URL + "/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.getStudentById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping(GROUPS_URL)
    public ResponseEntity<?> getGroups() {
        return ResponseEntity.ok(adminService.getGroups());
    }


    @GetMapping(GROUPS_URL + "/{id}")
    public ResponseEntity<?> getGroupById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.getGroupById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping(STUDENTS_URL)
    public ResponseEntity<?> createStudent(@Valid @RequestBody StudentDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.createStudent(dto));
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping(TEACHERS_URL)
    public ResponseEntity<?> createTeacher(@Valid @RequestBody TeacherDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.createTeacher(dto));
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PostMapping("/schedule")
    public ResponseEntity<?> createSchedule(@RequestBody ScheduleDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.createSchedule(dto));
    }


    @PostMapping(GROUPS_URL)
    public ResponseEntity<?> createGroup(@RequestParam("name") String name) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.addGroup(name));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
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
        } catch (IOException | CsvException | NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/upload-schedule")
    public ResponseEntity<?> uploadSchedule(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.uploadSchedule(file));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

    @PutMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResatPasswordDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.resetPasswordForUserById(dto));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping(TEACHERS_URL + "/{id}")
    public ResponseEntity<?> deleteTeacherById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.deleteTeacherById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping(STUDENTS_URL + "/{id}")
    public ResponseEntity<?> deleteStudentById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.deleteStudentById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping(GROUPS_URL + "/{id}")
    public ResponseEntity<?> deleteGroupById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(adminService.deleteGroupById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
