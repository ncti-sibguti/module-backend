package ru.ncti.modulebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ncti.modulebackend.entiny.Role;
import ru.ncti.modulebackend.entiny.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameOrEmail(String username, String email);

    List<User> findAllByRolesIn(Set<Role> roles);

}
