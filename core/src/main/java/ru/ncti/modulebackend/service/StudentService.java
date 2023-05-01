package ru.ncti.modulebackend.service;

import javassist.NotFoundException;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncti.modulebackend.entiny.Schedule;
import ru.ncti.modulebackend.entiny.Student;
import ru.ncti.modulebackend.repository.CertificateRepository;
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
    private final CertificateRepository certificateRepository;
    private final EmailSenderService emailSenderService;
    private final RabbitTemplate rabbitTemplate;

    public StudentService(UserRepository userRepository,
                          TeacherRepository teacherRepository,
                          StudentRepository studentRepository,
                          ScheduleRepository scheduleRepository,
                          GroupRepository groupRepository,
                          CertificateRepository certificateRepository,
                          EmailSenderService emailSenderService,
                          RabbitTemplate rabbitTemplate) {
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.scheduleRepository = scheduleRepository;
        this.groupRepository = groupRepository;
        this.certificateRepository = certificateRepository;
        this.emailSenderService = emailSenderService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional(readOnly = true)
    public Student getInfo() throws NotFoundException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return (Student) userRepository.findByUsernameOrEmail(userDetails.getUsername(), userDetails.getUsername())
                .orElseThrow(() -> {
                    log.error("User username  " + userDetails.getUsername() + " not fount");
                    return new NotFoundException("User " + userDetails.getUsername() + " not found");
                });
    }

    @Transactional(readOnly = true)
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

// part 2
//    public List<Certificate> getCertificates() {
//        return certificateRepository.findAll();
//    }
//
//    public String getCertificate(Long id) {
//        var auth = SecurityContextHolder.getContext().getAuthentication();
//        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
//        Student student = studentRepository.getById(userDetails.getUser().getId());
//
//        Certificate certificate = certificateRepository.getById(id);
//
//        Email email = new Email();
//        email.setTo(student.getEmail());
//        email.setSubject("Welcome Email from NCTI");
//        email.setTemplate("notification-email.html");
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("name", student.getFirstname());
//        properties.put("certificateType", certificate.getName());
//        properties.put("subscriptionDate", LocalDate.now().toString());
//        email.setProperties(properties);
//
//        rabbitTemplate.convertAndSend(CERTIFICATE_UPDATE, email);
//
//        return "OK";
//    }

}
