package ru.ncti.modulebackend.service;

import lombok.extern.log4j.Log4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.ncti.modulebackend.entiny.Student;
import ru.ncti.modulebackend.entiny.Teacher;
import ru.ncti.modulebackend.repository.TeacherRepository;
import ru.ncti.modulebackend.repository.UserRepository;
import ru.ncti.modulebackend.security.UserDetailsImpl;

import java.util.List;

@Service
@Log4j
public class StudentService {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;

    public StudentService(UserRepository userRepository, TeacherRepository teacherRepository) {
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
    }

    public Student getOne() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return (Student) userRepository.findByUsernameOrEmail(userDetails.getUsername(), userDetails.getUsername())
                .orElseThrow(() -> {
                    log.error("User username  " + userDetails.getUsername() + " not fount");
                    return new UsernameNotFoundException("User " + userDetails.getUsername() + " not found");
                });
    }

    public List<Teacher> getTeachers() {
        return teacherRepository.findAll();
    }


}
