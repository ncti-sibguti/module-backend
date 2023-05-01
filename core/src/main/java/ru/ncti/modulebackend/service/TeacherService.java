package ru.ncti.modulebackend.service;

import javassist.NotFoundException;
import lombok.extern.log4j.Log4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional(readOnly = true)
    public Teacher getInfo() throws NotFoundException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return (Teacher) userRepository
                .findById(userDetails.getUser().getId())
                .orElseThrow(() -> {
                    log.error("Teacher not found");
                    return new NotFoundException("Teacher not found");
                });
    }

    @Transactional(readOnly = true)
    public List<Schedule> getSchedule() throws NotFoundException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Teacher teacher = (Teacher) userRepository
                .findById(userDetails.getUser().getId())
                .orElseThrow(() -> {
                    log.error("Teacher not found");
                    return new NotFoundException("Teacher not found");
                });
        return teacher.getSchedules();
    }

}
