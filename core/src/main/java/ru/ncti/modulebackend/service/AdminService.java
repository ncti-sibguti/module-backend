package ru.ncti.modulebackend.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ncti.modulebackend.dto.NewsDTO;
import ru.ncti.modulebackend.dto.StudentDTO;
import ru.ncti.modulebackend.dto.TeacherDTO;
import ru.ncti.modulebackend.entiny.*;
import ru.ncti.modulebackend.exception.GroupNotFoundException;
import ru.ncti.modulebackend.exception.RoleNotFoundException;
import ru.ncti.modulebackend.repository.*;

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

    public AdminService(StudentRepository studentRepository,
                        ModelMapper modelMapper,
                        PasswordEncoder passwordEncoder,
                        GroupRepository groupRepository,
                        RoleRepository roleRepository,
                        TeacherRepository teacherRepository,
                        NewsRepository newsRepository) {
        this.studentRepository = studentRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.groupRepository = groupRepository;
        this.roleRepository = roleRepository;
        this.teacherRepository = teacherRepository;
        this.newsRepository = newsRepository;
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

    public News createNews(NewsDTO dto) {
        News news = convert(dto, News.class);
        newsRepository.save(news);
        return news;
    }

    private <S, D> D convert(S source, Class<D> dClass) {
        return modelMapper.map(source, dClass);
    }

}
