package ru.ncti.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ncti.backend.entiny.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
}
