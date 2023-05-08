package ru.ncti.backend.service;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncti.backend.dto.AdminDTO;
import ru.ncti.backend.entiny.Admin;
import ru.ncti.backend.entiny.Role;
import ru.ncti.backend.entiny.User;
import ru.ncti.backend.repository.RoleRepository;
import ru.ncti.backend.repository.UserRepository;
import ru.ncti.backend.security.AuthDTO;
import ru.ncti.backend.security.JwtTokenUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

@Service
@Log4j2
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       JwtTokenUtil jwtTokenUtil,
                       ModelMapper modelMapper,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       UserService userService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @Transactional(readOnly = false)
    public User register(AdminDTO dto) {
        if (userRepository.findByUsernameOrEmail(dto.getUsername(), dto.getUsername()).isPresent()) {
            throw new UsernameNotFoundException("User " + dto.getUsername() + " already exist");
        }
        User candidate = convert(dto, Admin.class);

        Role role = roleRepository.findByName("ROLE_ADMIN").orElse(null);

        candidate.setRoles(Set.of(role));
        candidate.setPassword(passwordEncoder.encode(dto.getPassword()));

        userRepository.save(candidate);
        return candidate;
    }

    @Transactional(readOnly = true)
    public Map<String, String> login(AuthDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        var userDetails = (User) userService.loadUserByUsername(dto.getUsername());

        String accessToken = jwtTokenUtil.generateToken(userDetails);
        String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    @Transactional(readOnly = true)
    public Map<String, String> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String authToken = request.getHeader("Authorization");
        final String token = authToken.substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        User userDetails = (User) userService.loadUserByUsername(username);

        if (jwtTokenUtil.validateRefreshToken(token, userDetails)) {
            String accessToken = jwtTokenUtil.generateToken(userDetails);
            String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }
        return null;
    }

    private <S, D> D convert(S source, Class<D> dClass) {
        return modelMapper.map(source, dClass);
    }

}
