package ru.ncti.backend.service;

import javassist.NotFoundException;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncti.backend.entiny.Group;
import ru.ncti.backend.entiny.Schedule;
import ru.ncti.backend.entiny.Student;
import ru.ncti.backend.entiny.enums.WeekType;
import ru.ncti.backend.repository.CertificateRepository;
import ru.ncti.backend.repository.GroupRepository;
import ru.ncti.backend.repository.ScheduleRepository;
import ru.ncti.backend.repository.StudentRepository;
import ru.ncti.backend.repository.TeacherRepository;
import ru.ncti.backend.repository.UserRepository;
import ru.ncti.backend.security.UserDetailsImpl;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
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
    public Student getProfile() throws NotFoundException {
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

        Set<Schedule> currSchedule = getTypeSchedule(student.getGroup());

        for (Schedule s : currSchedule) {
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

    private Set<Schedule> getTypeSchedule(Group group) {
        List<Schedule> schedule = scheduleRepository.findAllByGroup(group);
        WeekType currentWeekType = getCurrentWeekType();
        return schedule.stream()
                .filter(s -> s.getType() == WeekType.CONST || s.getType() == currentWeekType)
                .collect(Collectors.toSet());
    }

    private WeekType getCurrentWeekType() {
        LocalDate currentDate = LocalDate.now();
        int currentWeekNumber = currentDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        return currentWeekNumber % 2 == 0 ? WeekType.EVEN : WeekType.ODD;
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
