package ru.ncti.modulebackend.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ncti.modulebackend.dto.AdminDTO;
import ru.ncti.modulebackend.dto.NewsDTO;
import ru.ncti.modulebackend.dto.StudentDTO;
import ru.ncti.modulebackend.dto.SubjectDTO;
import ru.ncti.modulebackend.dto.TeacherDTO;
import ru.ncti.modulebackend.entiny.Admin;
import ru.ncti.modulebackend.entiny.Group;
import ru.ncti.modulebackend.entiny.News;
import ru.ncti.modulebackend.entiny.Role;
import ru.ncti.modulebackend.entiny.Student;
import ru.ncti.modulebackend.entiny.Subject;
import ru.ncti.modulebackend.entiny.Teacher;
import ru.ncti.modulebackend.exception.GroupNotFoundException;
import ru.ncti.modulebackend.exception.RoleNotFoundException;
import ru.ncti.modulebackend.repository.AdminRepository;
import ru.ncti.modulebackend.repository.GroupRepository;
import ru.ncti.modulebackend.repository.NewsRepository;
import ru.ncti.modulebackend.repository.RoleRepository;
import ru.ncti.modulebackend.repository.StudentRepository;
import ru.ncti.modulebackend.repository.SubjectRepository;
import ru.ncti.modulebackend.repository.TeacherRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
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

    public AdminService(StudentRepository studentRepository,
                        ModelMapper modelMapper,
                        PasswordEncoder passwordEncoder,
                        GroupRepository groupRepository,
                        RoleRepository roleRepository,
                        TeacherRepository teacherRepository,
                        NewsRepository newsRepository,
                        SubjectRepository subjectRepository,
                        AdminRepository adminRepository) {
        this.studentRepository = studentRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.groupRepository = groupRepository;
        this.roleRepository = roleRepository;
        this.teacherRepository = teacherRepository;
        this.newsRepository = newsRepository;
        this.subjectRepository = subjectRepository;
        this.adminRepository = adminRepository;
    }

    public AdminDTO getInfoById(Long id) {
        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin != null) {
            return convert(admin, AdminDTO.class);
        }
        return null;
    }

    public Student createStudent(StudentDTO dto) throws Exception {
        Student student = convert(dto, Student.class);

        Role role = roleRepository.findByName("ROLE_STUDENT")
                .orElseThrow(() -> new RoleNotFoundException("ROLE_STUDENT not found"));
        Group group = groupRepository.findById(dto.getGroupId())
                .orElseThrow(() -> new GroupNotFoundException("Group not found"));
        student.setGroup(group);
        student.setPassword(passwordEncoder.encode(dto.getPassword()));
        student.setRoles(Set.of(role));

        studentRepository.save(student);

        return student;
    }

    public Teacher createTeacher(TeacherDTO dto) {
        Teacher teacher = convert(dto, Teacher.class);
        Role role = roleRepository.findByName("ROLE_TEACHER").orElseThrow(null);
        teacher.setRoles(Set.of(role));
        teacher.setPassword(passwordEncoder.encode(dto.getPassword()));

        teacherRepository.save(teacher);
        return teacher;
    }

    public Subject createSubject(SubjectDTO dto) {
        Subject subject = convert(dto, Subject.class);
        teacherRepository.findById(dto.getTeacher()).ifPresent(subject::setTeacher);
        subjectRepository.save(subject);
        return subject;
    }

    public News createNews(NewsDTO dto) {
        News news = convert(dto, News.class);
        newsRepository.save(news);
        return news;
    }

    private <S, D> D convert(S source, Class<D> dClass) {
        return modelMapper.map(source, dClass);
    }

    public List<Teacher> getTeachers() {
        return teacherRepository.findAll();
    }
}
