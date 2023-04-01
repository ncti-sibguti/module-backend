package ru.ncti.modulebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ncti.modulebackend.entiny.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
