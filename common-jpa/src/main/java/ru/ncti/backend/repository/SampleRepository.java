package ru.ncti.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ncti.backend.entiny.Group;
import ru.ncti.backend.entiny.Sample;
import ru.ncti.backend.entiny.users.Teacher;

import java.util.List;

@Repository
public interface SampleRepository extends JpaRepository<Sample, Long> {
    List<Sample> findAllByTeacher(Teacher teacher);

    List<Sample> findAllByGroup(Group group);
}
