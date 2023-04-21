package ru.ncti.modulebackend.service;

import javassist.NotFoundException;
import lombok.extern.log4j.Log4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.ncti.modulebackend.entiny.Schedule;
import ru.ncti.modulebackend.entiny.Student;
import ru.ncti.modulebackend.repository.GroupRepository;
import ru.ncti.modulebackend.repository.ScheduleRepository;
import ru.ncti.modulebackend.repository.StudentRepository;
import ru.ncti.modulebackend.repository.TeacherRepository;
import ru.ncti.modulebackend.repository.UserRepository;
import ru.ncti.modulebackend.security.UserDetailsImpl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j
public class StudentService {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final ScheduleRepository scheduleRepository;
    private final GroupRepository groupRepository;

    public StudentService(UserRepository userRepository,
                          TeacherRepository teacherRepository,
                          StudentRepository studentRepository,
                          ScheduleRepository scheduleRepository,
                          GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.scheduleRepository = scheduleRepository;
        this.groupRepository = groupRepository;
    }

    public Student getInfo() throws NotFoundException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return (Student) userRepository.findByUsernameOrEmail(userDetails.getUsername(), userDetails.getUsername())
                .orElseThrow(() -> {
                    log.error("User username  " + userDetails.getUsername() + " not fount");
                    return new NotFoundException("User " + userDetails.getUsername() + " not found");
                });
    }

    public Map<String, Set<Schedule>> getSchedule() throws NotFoundException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Student student = (Student) userRepository
                .findByUsernameOrEmail(userDetails.getUsername(), userDetails.getUsername())
                .orElseThrow(() -> {
                    log.error("User username  " + userDetails.getUsername() + " not fount");
                    return new NotFoundException("User " + userDetails.getUsername() + " not found");
                });

        Map<String, Set<Schedule>> map = new HashMap<>();

        for (Schedule s : student.getGroup().getSchedule()) {
            map.computeIfAbsent(s.getDay(), k -> new HashSet<>()).add(s);
        }

        map.forEach((key, value) -> {
            Set<Schedule> sortedSet = value.stream()
                    .sorted(Comparator.comparingInt(Schedule::getNumberPair))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            map.put(key, sortedSet);
        });

        return map;
    }

}
