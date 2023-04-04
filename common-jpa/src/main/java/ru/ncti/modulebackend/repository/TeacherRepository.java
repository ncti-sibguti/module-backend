package ru.ncti.modulebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ncti.modulebackend.entiny.Teacher;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
