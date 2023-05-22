package ru.ncti.backend.service;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.log4j.Log4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.ncti.backend.dto.AdminDTO;
import ru.ncti.backend.dto.ResetPasswordDTO;
import ru.ncti.backend.dto.GroupDTO;
import ru.ncti.backend.dto.SampleDTO;
import ru.ncti.backend.dto.ScheduleUploadDTO;
import ru.ncti.backend.dto.StudentDTO;
import ru.ncti.backend.dto.SubjectDTO;
import ru.ncti.backend.dto.TeacherDTO;
import ru.ncti.backend.dto.UserDTO;
import ru.ncti.backend.entiny.Group;
import ru.ncti.backend.entiny.Role;
import ru.ncti.backend.entiny.Sample;
import ru.ncti.backend.entiny.Subject;
import ru.ncti.backend.entiny.User;
import ru.ncti.backend.entiny.users.Admin;
import ru.ncti.backend.entiny.users.Student;
import ru.ncti.backend.entiny.users.Teacher;
import ru.ncti.backend.model.Email;
import ru.ncti.backend.repository.AdminRepository;
import ru.ncti.backend.repository.GroupRepository;
import ru.ncti.backend.repository.RoleRepository;
import ru.ncti.backend.repository.SampleRepository;
import ru.ncti.backend.repository.StudentRepository;
import ru.ncti.backend.repository.SubjectRepository;
import ru.ncti.backend.repository.TeacherRepository;
import ru.ncti.backend.repository.UserRepository;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static ru.ncti.backend.rabbitmq.model.RabbitQueue.EMAIL_UPDATE;

@Service
@Log4j
public class AdminService {

    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final GroupRepository groupRepository;
    private final RoleRepository roleRepository;
    private final TeacherRepository teacherRepository;
    private final AdminRepository adminRepository;
    private final SampleRepository sampleRepository;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    private final SubjectRepository subjectRepository;

    public AdminService(StudentRepository studentRepository,
                        ModelMapper modelMapper,
                        PasswordEncoder passwordEncoder,
                        GroupRepository groupRepository,
                        RoleRepository roleRepository,
                        TeacherRepository teacherRepository,
                        AdminRepository adminRepository,
                        SampleRepository sampleRepository,
                        UserRepository userRepository,
                        RabbitTemplate rabbitTemplate,
                        SubjectRepository subjectRepository) {
        this.studentRepository = studentRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.groupRepository = groupRepository;
        this.roleRepository = roleRepository;
        this.teacherRepository = teacherRepository;
        this.adminRepository = adminRepository;
        this.sampleRepository = sampleRepository;
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.subjectRepository = subjectRepository;
    }

    @Transactional(readOnly = true)
    public UserDTO getInfoById(Long id) {
        Admin admin = adminRepository.getById(id);
        return convert(admin, UserDTO.class);
    }

    @Transactional(readOnly = false)
    public Admin updatePasswordForAdminById(Long id, AdminDTO dto) {
        Admin admin = adminRepository.findById(id).orElseThrow(() -> {
            log.error("Admin with id " + id + " not found");
            return new UsernameNotFoundException("Admin with id " + id + " not found");
        });
        admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        adminRepository.save(admin);
        return admin;
    }

    @Transactional(readOnly = false)
    public Student createStudent(StudentDTO dto) {
        Student student = convert(dto, Student.class);

        Role role = roleRepository.findByName("ROLE_STUDENT")
                .orElseThrow(() -> {
                    log.error("ROLE_STUDENT not found");
                    return new UsernameNotFoundException("ROLE_STUDENT not found");
                });
        Group group = groupRepository.findByName(dto.getGroup())
                .orElseThrow(() -> {
                    log.error("Group " + dto.getGroup() + " not found");
                    return new UsernameNotFoundException("Group not found");
                });
        student.setGroup(group);
        student.setPassword(passwordEncoder.encode(dto.getPassword()));
        student.setRoles(Set.of(role));

        studentRepository.save(student);

//        createEmailNotification(student, dto.getPassword());

        return student;
    }

    @Transactional(readOnly = false)
    public Teacher createTeacher(TeacherDTO dto) {
        Teacher teacher = convert(dto, Teacher.class);
        Role role = roleRepository.findByName("ROLE_TEACHER")
                .orElseThrow(() -> {
                    log.error("ROLE_TEACHER not found");
                    return new UsernameNotFoundException("ROLE_TEACHER not found");
                });
        teacher.setRoles(Set.of(role));
        teacher.setPassword(passwordEncoder.encode(dto.getPassword()));

        teacherRepository.save(teacher);

//        createEmailNotification(teacher, dto.getPassword());

        return teacher;
    }


    @Transactional(readOnly = false)
    public String createSchedule(SampleDTO dto) {
        Group g = groupRepository.getById(dto.getGroup());
        Teacher teacher = teacherRepository.getById(dto.getTeacher());
        Subject subject = subjectRepository.getById(dto.getSubject());

        Sample sample = Sample.builder()
                .day(dto.getDay())
                .group(g)
                .numberPair(dto.getNumberPair())
                .teacher(teacher)
                .subject(subject)
                .classroom(dto.getClassroom())
                .parity(dto.getParity())
                .build();
        sampleRepository.save(sample);
        return "OK";
    }

    @Transactional(readOnly = true)
    public List<Teacher> getTeachers() {
        return teacherRepository.findAllByOrderByLastname();
    }

    @Transactional(readOnly = false)
    public String addGroup(GroupDTO dto) throws Exception {
        if (groupRepository.findByName(dto.getName()).isPresent()) {
            log.error("Group" + dto.getName() + " already exist");
            throw new Exception("Group" + dto.getName() + " already exist");
        }

        Group group = convert(dto, Group.class);
        groupRepository.save(group);
        return "Group was created";
    }

    @Transactional(readOnly = false)
    public String uploadStudents(MultipartFile file) throws IOException, CsvValidationException {
        CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        List<StudentDTO> students = new CsvToBeanBuilder<StudentDTO>(csvReader)
                .withType(StudentDTO.class).build().parse();

        List<CompletableFuture<Void>> futures = students.stream()
                .map(student -> CompletableFuture.runAsync(() -> {
                    try {
                        createStudent(student);
                    } catch (UsernameNotFoundException e) {
                        log.error(e);
                        throw new RuntimeException(e);
                    }
                })).toList();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.join();
        csvReader.close();
        log.info("Uploaded students");
        return "Uploaded students";
    }

    @Transactional
    public String uploadTeacher(MultipartFile file) throws IOException, CsvValidationException {
        CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        List<TeacherDTO> teachers = new CsvToBeanBuilder<TeacherDTO>(csvReader)
                .withType(TeacherDTO.class).build().parse();

        List<CompletableFuture<Void>> futures = teachers.stream()
                .map(teacher -> CompletableFuture.runAsync(() -> {
                    try {
                        createTeacher(teacher);
                    } catch (UsernameNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }))
                .toList();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.join();
        csvReader.close();

        log.info("Uploaded teachers");
        return "Uploaded teachers";
    }

    @Transactional
    public String uploadSchedule(MultipartFile file) throws IOException {
        CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        List<ScheduleUploadDTO> schedule = new CsvToBeanBuilder<ScheduleUploadDTO>(csvReader)
                .withType(ScheduleUploadDTO.class).build().parse();

        List<CompletableFuture<Void>> futures = schedule.stream()
                .map(s -> CompletableFuture.runAsync(() -> {
                    Group g = groupRepository.findByName(s.getGroup())
                            .orElseThrow(() -> {
                                log.error("Group " + s.getGroup() + " not found");
                                return new IllegalArgumentException("Group " + s.getGroup() + " not found");
                            });
                    String[] teacherName = s.getTeacher().split(" ");
                    Teacher t = teacherRepository.findByLastnameAndFirstname(teacherName[0], teacherName[1])
                            .orElseThrow(() -> {
                                log.error("Teacher " + s.getTeacher() + " not found");
                                return new IllegalArgumentException("Teacher " + s.getTeacher() + " not found");
                            });

                    Sample sch = convert(s, Sample.class);
                    Subject subject = subjectRepository.findByName(s.getSubject()).orElse(null);
                    sch.setGroup(g);
                    sch.setTeacher(t);
                    sch.setParity(s.getWeekType());
                    sch.setSubject(subject);
                    sampleRepository.save(sch);
                })).toList();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.join();
        csvReader.close();

        log.info("Uploaded schedule");
        return "Uploaded schedule";
    }

    @Transactional(readOnly = true)
    public List<Student> getStudents(Long group) {
        Group g = groupRepository.findById(group).orElseThrow(() -> {
            log.error("Group with id " + group + " not found");
            return new UsernameNotFoundException("Group with id " + group + " not found");
        });
        return studentRepository.findAllByGroupOrderByLastname(g);
    }

    @Transactional(readOnly = true)
    public List<Group> getGroups() {
        return groupRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id).orElseThrow(() -> {
            log.error("Teacher with id " + id + " not found");
            return new UsernameNotFoundException("Teacher with id + " + id + " not found");
        });
    }

    @Transactional(readOnly = true)
    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> {
            log.error("Student with id " + id + " not found");
            return new UsernameNotFoundException("Student with id + " + id + " not found");
        });
    }

    @Transactional(readOnly = true)
    public Group getGroupById(Long id) {
        Group g = groupRepository.findById(id).orElseThrow(() -> {
            log.error("Group with id " + id + " not found");
            return new UsernameNotFoundException("Teacher with id + " + id + " not found");
        });
        g.setSample(getTypeSchedule(g));

        return g;
    }

    @Transactional(readOnly = false)
    public String deleteStudentById(Long id) {
        Student student = studentRepository.getById(id);

        student.getRoles().forEach(r -> r.getUsers().remove(student));
        student.getRoles().clear();
        studentRepository.save(student);

        studentRepository.delete(student);
        return "Student successfully deleted";
    }

    @Transactional(readOnly = false)
    public String deleteTeacherById(Long id) {
        Teacher teacher = teacherRepository.getById(id);

        teacher.getRoles().forEach(r -> r.getUsers().remove(teacher));
        teacher.getRoles().clear();
        teacherRepository.save(teacher);

        teacherRepository.delete(teacher);
        return "Teacher successfully deleted";
    }

    @Transactional(readOnly = false)
    public String deleteGroupById(Long id) {
        Group group = groupRepository.findById(id).orElseThrow(() -> {
            log.error("Group with id " + id + " not found");
            return new UsernameNotFoundException("Group with id + " + id + " not found");
        });
        groupRepository.delete(group);
        return "Group successfully deleted";
    }

    @Transactional(readOnly = false)
    public String resetPasswordForUserById(ResetPasswordDTO dto) {
        //todo: add send email with changed password
        User candidate = userRepository.findById(dto.getId()).orElseThrow(() -> {
            log.error("User with id " + dto.getPassword() + "not found");
            return new UsernameNotFoundException("User with id " + dto.getPassword() + "not found");
        });
        candidate.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(candidate);
        return "Password update";
    }

    @Transactional(readOnly = false)
    public Subject addSubject(SubjectDTO dto) {
        return subjectRepository.save(convert(dto, Subject.class));
    }

    @Transactional(readOnly = true)
    public List<Subject> getSubjects() {
        return subjectRepository.findAll();
    }

    private <S, D> D convert(S source, Class<D> dClass) {
        return modelMapper.map(source, dClass);
    }

    private Set<Sample> getTypeSchedule(Group group) {
        List<Sample> sample = sampleRepository.findAllByGroup(group);
        String currentWeekType = getCurrentWeekType();
        return sample.stream()
                .filter(s -> s.getParity().equals("0") || s.getParity().equals(currentWeekType))
                .collect(Collectors.toSet());
    }

    private String getCurrentWeekType() {
        LocalDate currentDate = LocalDate.now();
        int currentWeekNumber = currentDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        return currentWeekNumber % 2 == 0 ? "2" : "1";
    }

    private void createEmailNotification(User dto, String password) {
        Email email = Email.builder()
                .to(dto.getEmail())
                .subject("Добро пожаловать в мобильное приложение.")
                .template("welcome-email.html")
                .properties(new HashMap<>() {{
                    put("name", dto.getFirstname());
                    put("subscriptionDate", LocalDate.now().toString());
                    put("login", dto.getUsername());
                    put("password", password);
                }})
                .build();

        rabbitTemplate.convertAndSend(EMAIL_UPDATE, email);
    }
}
