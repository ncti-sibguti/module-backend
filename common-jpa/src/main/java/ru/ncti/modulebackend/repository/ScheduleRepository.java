package ru.ncti.modulebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ncti.modulebackend.entiny.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
