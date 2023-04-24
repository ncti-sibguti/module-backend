package ru.ncti.modulebackend.service;

import javassist.NotFoundException;
import lombok.extern.log4j.Log4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.ncti.modulebackend.entiny.Schedule;
import ru.ncti.modulebackend.entiny.Teacher;
import ru.ncti.modulebackend.repository.ScheduleRepository;
import ru.ncti.modulebackend.repository.TeacherRepository;
import ru.ncti.modulebackend.repository.UserRepository;
import ru.ncti.modulebackend.security.UserDetailsImpl;

import java.util.List;

@Service
@Log4j
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public TeacherService(TeacherRepository teacherRepository,
                          ScheduleRepository scheduleRepository,
                          UserRepository userRepository) {
        this.teacherRepository = teacherRepository;
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
    }

    public Teacher getInfo() throws NotFoundException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return (Teacher) userRepository
//                .findByUsernameOrEmail(userDetails.getUser().getUsername(), userDetails.getUser().getEmail()) -> not work
                .findByUsernameOrEmail(userDetails.getUser().getEmail(), userDetails.getUser().getEmail())
                .orElseThrow(() -> {
                    log.error("Teacher not found");
                    return new NotFoundException("Teacher not found");
                });
    }

    public List<Schedule> getSchedule() throws NotFoundException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        log.info(userDetails.getUser().getEmail());
        Teacher teacher = (Teacher) userRepository
//                .findByUsernameOrEmail(userDetails.getUser().getUsername(), userDetails.getUser().getEmail()) -> not work
                .findByUsernameOrEmail(userDetails.getUser().getEmail(), userDetails.getUser().getEmail())
                .orElseThrow(() -> {
                    log.error("Teacher not found");
                    return new NotFoundException("Teacher not found");
                });
        return scheduleRepository.findAllByTeacher(teacher);
    }

}
