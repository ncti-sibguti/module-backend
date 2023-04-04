package ru.ncti.modulebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ncti.modulebackend.entiny.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
