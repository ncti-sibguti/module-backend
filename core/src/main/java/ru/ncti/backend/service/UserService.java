package ru.ncti.backend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncti.backend.dto.UserDTO;
import ru.ncti.backend.repository.RoleRepository;
import ru.ncti.backend.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found"));
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getUsers(String type) {
//        if (type != null) {
//            switch (type) {
//                case "student" -> {
//                    Optional<Role> role = roleRepository.findByName("ROLE_STUDENT");
//                    if (role.isPresent())
//                        return userRepository.findAllByRolesIn(Set.of(role.get()));
//                    else
//                        return Collections.emptyList();
//                }
//                case "teacher" -> {
//                    Optional<Role> role = roleRepository.findByName("ROLE_TEACHER");
//                    if (role.isPresent())
//                        return userRepository.findAllByRolesIn(Set.of(role.get()));
//                    else
//                        return Collections.emptyList();
//                }
//            }
//
//        }

        List<UserDTO> users = new ArrayList<>();

        userRepository.findAll().forEach(user -> {
            users.add(UserDTO.builder()
                    .id(user.getId())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .surname(user.getSurname())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .build());
        });

        return users;
    }
}
