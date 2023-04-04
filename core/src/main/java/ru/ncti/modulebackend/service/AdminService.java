package ru.ncti.modulebackend.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ncti.modulebackend.dto.StudentDTO;
import ru.ncti.modulebackend.dto.TeacherDTO;
import ru.ncti.modulebackend.entiny.Group;
import ru.ncti.modulebackend.entiny.Role;
import ru.ncti.modulebackend.entiny.Student;
import ru.ncti.modulebackend.entiny.Teacher;
import ru.ncti.modulebackend.repository.GroupRepository;
import ru.ncti.modulebackend.repository.RoleRepository;
import ru.ncti.modulebackend.repository.StudentRepository;
import ru.ncti.modulebackend.repository.TeacherRepository;

import java.util.Set;

@Service
public class AdminService {

    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final GroupRepository groupRepository;
    private final RoleRepository roleRepository;
    private final TeacherRepository teacherRepository;

    public AdminService(StudentRepository studentRepository,
                        ModelMapper modelMapper,
                        PasswordEncoder passwordEncoder,
                        GroupRepository groupRepository,
                        RoleRepository roleRepository,
                        TeacherRepository teacherRepository) {
        this.studentRepository = studentRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.groupRepository = groupRepository;
        this.roleRepository = roleRepository;
        this.teacherRepository = teacherRepository;
    }


    // TODO: create custom exceptions
    public Student createStudent(StudentDTO dto) {
        Student student = convert(dto, Student.class);

        Role role = roleRepository.findByName("ROLE_STUDENT").orElseThrow(null);
        Group group = groupRepository.findById(dto.getGroupId()).orElseThrow(null);
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

    private <S, D> D convert(S source, Class<D> dClass) {
        return modelMapper.map(source, dClass);
    }

}
