package ru.ncti.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ncti.backend.entiny.Group;
import ru.ncti.backend.entiny.Schedule;
import ru.ncti.backend.entiny.Teacher;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByTeacher(Teacher teacher);

    List<Schedule> findAllByGroup(Group group);
}
