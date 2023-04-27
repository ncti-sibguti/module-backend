package ru.ncti.modulebackend.service;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import javassist.NotFoundException;
import lombok.extern.log4j.Log4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.ncti.modulebackend.dto.AdminDTO;
import ru.ncti.modulebackend.dto.GroupDTO;
import ru.ncti.modulebackend.dto.NewsDTO;
import ru.ncti.modulebackend.dto.ResatPasswordDTO;
import ru.ncti.modulebackend.dto.ScheduleDTO;
import ru.ncti.modulebackend.dto.StudentDTO;
import ru.ncti.modulebackend.dto.SubjectDTO;
import ru.ncti.modulebackend.dto.TeacherDTO;
import ru.ncti.modulebackend.dto.UserDTO;
import ru.ncti.modulebackend.entiny.Admin;
import ru.ncti.modulebackend.entiny.Group;
import ru.ncti.modulebackend.entiny.News;
import ru.ncti.modulebackend.entiny.Role;
import ru.ncti.modulebackend.entiny.Schedule;
import ru.ncti.modulebackend.entiny.Student;
import ru.ncti.modulebackend.entiny.Subject;
import ru.ncti.modulebackend.entiny.Teacher;
import ru.ncti.modulebackend.entiny.User;
import ru.ncti.modulebackend.repository.AdminRepository;
import ru.ncti.modulebackend.repository.GroupRepository;
import ru.ncti.modulebackend.repository.NewsRepository;
import ru.ncti.modulebackend.repository.RoleRepository;
import ru.ncti.modulebackend.repository.ScheduleRepository;
import ru.ncti.modulebackend.repository.StudentRepository;
import ru.ncti.modulebackend.repository.SubjectRepository;
import ru.ncti.modulebackend.repository.TeacherRepository;
import ru.ncti.modulebackend.repository.UserRepository;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j
public class AdminService {

    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final GroupRepository groupRepository;
    private final RoleRepository roleRepository;
    private final TeacherRepository teacherRepository;
    private final NewsRepository newsRepository;
    private final SubjectRepository subjectRepository;
    private final AdminRepository adminRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public AdminService(StudentRepository studentRepository,
                        ModelMapper modelMapper,
                        PasswordEncoder passwordEncoder,
                        GroupRepository groupRepository,
                        RoleRepository roleRepository,
                        TeacherRepository teacherRepository,
                        NewsRepository newsRepository,
                        SubjectRepository subjectRepository,
                        AdminRepository adminRepository,
                        ScheduleRepository scheduleRepository,
                        UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.groupRepository = groupRepository;
        this.roleRepository = roleRepository;
        this.teacherRepository = teacherRepository;
        this.newsRepository = newsRepository;
        this.subjectRepository = subjectRepository;
        this.adminRepository = adminRepository;
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public AdminDTO getInfoById(Long id) {
        Admin admin = adminRepository.getById(id);
        return convert(admin, AdminDTO.class);
    }

    @Transactional(readOnly = false)
    public Admin updateData(Long id, UserDTO dto) throws NotFoundException {
        Admin admin = adminRepository.findById(id).orElseThrow(() -> {
            log.error("Admin with id " + id + " not found");
            return new NotFoundException("Admin with id " + id + " not found");
        });

        admin.setFirstname(dto.getFirstname());
        admin.setLastname(dto.getLastname());
        admin.setSurname(dto.getSurname());
        admin.setEmail(dto.getEmail());
        admin.setUsername(dto.getUsername());
        admin.setPassword(passwordEncoder.encode(dto.getPassword()));

        adminRepository.save(admin);
        return admin;
    }

    @Transactional(readOnly = false)
    public Student createStudent(StudentDTO dto) throws NotFoundException {
        Student student = convert(dto, Student.class);

        Role role = roleRepository.findByName("ROLE_STUDENT")
                .orElseThrow(() -> {
                    log.error("ROLE_STUDENT not found");
                    return new NotFoundException("ROLE_STUDENT not found");
                });
        Group group = groupRepository.findByName(dto.getGroup())
                .orElseThrow(() -> {
                    log.error("Group " + dto.getGroup() + " not found");
                    return new NotFoundException("Group not found");
                });
        student.setGroup(group);
        student.setPassword(passwordEncoder.encode(dto.getPassword()));
        student.setRoles(Set.of(role));

        studentRepository.save(student);

        return student;
    }

    @Transactional(readOnly = false)
    public Teacher createTeacher(TeacherDTO dto) throws NotFoundException {
        Teacher teacher = convert(dto, Teacher.class);
        Role role = roleRepository.findByName("ROLE_TEACHER")
                .orElseThrow(() -> {
                    log.error("ROLE_TEACHER not found");
                    return new NotFoundException("ROLE_TEACHER not found");
                });
        teacher.setRoles(Set.of(role));
        teacher.setPassword(passwordEncoder.encode(dto.getPassword()));

        teacherRepository.save(teacher);
        return teacher;
    }

    @Transactional(readOnly = false)
    public Subject createSubject(SubjectDTO dto) {
        return subjectRepository.save(convert(dto, Subject.class));
    }

    @Transactional(readOnly = false)
    public String createSchedule(ScheduleDTO dto) {
        Group g = groupRepository.getById(dto.getGroup());
        Subject subject = subjectRepository.getById(dto.getSubject());
        Teacher teacher = teacherRepository.getById(dto.getTeacher());
        Schedule schedule = Schedule.builder()
                .day(dto.getDay())
                .group(g)
                .numberPair(dto.getNumberPair())
                .teacher(teacher)
                .subject(subject)
                .classroom(dto.getClassroom())
                .build();
        scheduleRepository.save(schedule);
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
    public News createNews(NewsDTO dto) {
        News news = convert(dto, News.class);
        newsRepository.save(news);
        return news;
    }

    @Transactional(readOnly = false)
    public String uploadStudents(MultipartFile file) throws IOException, CsvValidationException {
        CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        List<StudentDTO> students = new CsvToBeanBuilder<StudentDTO>(csvReader)
                .withType(StudentDTO.class).build().parse();

        List<CompletableFuture<Void>> futures = students.stream()
                .map(student -> CompletableFuture.runAsync(() -> {
                    Student s = convert(student, Student.class);
                    s.setPassword(passwordEncoder.encode(student.getPassword()));
                    roleRepository.findByName("ROLE_STUDENT").ifPresent(role -> s.setRoles(Set.of(role)));
                    groupRepository.findByName(student.getGroup()).ifPresent(s::setGroup);
                    studentRepository.save(s);
                })).toList();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.join();
        csvReader.close();
        log.info("Uploaded students");
        return "Uploaded students";
    }

    @Transactional
    public String uploadTeacher(MultipartFile file) throws IOException, CsvValidationException, NotFoundException {
        CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        List<TeacherDTO> teachers = new CsvToBeanBuilder<TeacherDTO>(csvReader)
                .withType(TeacherDTO.class).build().parse();

        List<CompletableFuture<Void>> futures = teachers.stream()
                .map(teacher -> CompletableFuture.runAsync(() -> {
                    Teacher t = convert(teacher, Teacher.class);
                    t.setPassword(passwordEncoder.encode(teacher.getPassword()));
                    roleRepository.findByName("ROLE_TEACHER").ifPresent(role -> t.setRoles(Set.of(role)));
                    teacherRepository.save(t);
                }))
                .toList();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.join();
        csvReader.close();

        log.info("Uploaded teachers");
        return "Uploaded teachers";
    }

    @Transactional(readOnly = true)
    public List<Student> getStudents(Long group) throws NotFoundException {
        Group g = groupRepository.findById(group).orElseThrow(() -> {
            log.error("Group with id " + group + " not found");
            return new NotFoundException("Group with id " + group + " not found");
        });
        return studentRepository.findAllByGroupOrderByLastname(g);
    }

    @Transactional(readOnly = true)
    public List<Group> getGroups() {
        return groupRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Teacher getTeacherById(Long id) throws NotFoundException {
        return teacherRepository.findById(id).orElseThrow(() -> {
            log.error("Teacher with id " + id + " not found");
            return new NotFoundException("Teacher with id + " + id + " not found");
        });
    }

    @Transactional(readOnly = true)
    public Student getStudentById(Long id) throws NotFoundException {
        return studentRepository.findById(id).orElseThrow(() -> {
            log.error("Student with id " + id + " not found");
            return new NotFoundException("Student with id + " + id + " not found");
        });
    }

    @Transactional(readOnly = true)
    public Group getGroupById(Long id) throws NotFoundException {
        return groupRepository.findById(id).orElseThrow(() -> {
            log.error("Group with id " + id + " not found");
            return new NotFoundException("Teacher with id + " + id + " not found");
        });
    }

    @Transactional(readOnly = false)
    public String deleteStudentById(Long id) throws NotFoundException {
        Student student = studentRepository.getById(id);

        student.getRoles().forEach(r -> r.getUsers().remove(student));
        student.getRoles().clear();
        studentRepository.save(student);

        studentRepository.delete(student);
        return "Student successfully deleted";
    }

    @Transactional(readOnly = false)
    public String deleteTeacherById(Long id) throws NotFoundException {
        Teacher teacher = teacherRepository.getById(id);

        teacher.getRoles().forEach(r -> r.getUsers().remove(teacher));
        teacher.getRoles().clear();
        teacherRepository.save(teacher);

        teacherRepository.delete(teacher);
        return "Teacher successfully deleted";
    }

    @Transactional(readOnly = false)
    public String deleteGroupById(Long id) throws NotFoundException {
        Group group = groupRepository.findById(id).orElseThrow(() -> {
            log.error("Group with id " + id + " not found");
            return new NotFoundException("Group with id + " + id + " not found");
        });
        groupRepository.delete(group);
        return "Group successfully deleted";
    }

    @Transactional(readOnly = true)
    public List<Subject> getSubjects() {
        return subjectRepository.findAll();
    }

    @Transactional(readOnly = false)
    public String resetPasswordForUserById(ResatPasswordDTO dto) throws NotFoundException {
        //todo: add send email with changed password
        User candidate = userRepository.findById(dto.getId()).orElseThrow(() -> {
            log.error("User with id " + dto.getPassword() + "not found");
            return new NotFoundException("User with id " + dto.getPassword() + "not found");
        });
        candidate.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(candidate);
        return "Password update";
    }

    private <S, D> D convert(S source, Class<D> dClass) {
        return modelMapper.map(source, dClass);
    }
}
