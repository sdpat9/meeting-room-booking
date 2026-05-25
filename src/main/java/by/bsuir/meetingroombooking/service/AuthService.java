package by.bsuir.meetingroombooking.service;

import by.bsuir.meetingroombooking.model.Role;
import by.bsuir.meetingroombooking.model.User;
import by.bsuir.meetingroombooking.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import by.bsuir.meetingroombooking.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public User register(String name, String email, String password) {
        log.info("Registering user: email={}", email);

        if (userRepository.existsByEmail(email)) {
            log.warn("Registration failed: email already in use, email={}", email);
            throw new IllegalStateException("email is already in use: " + email);
        }

        String encodedPassword = passwordEncoder.encode(password);

        User user = new User(name, email, encodedPassword, true, Role.USER);
        User savedUser = userRepository.save(user);

        log.info("User registered successfully: userId={}, email={}", savedUser.getId(), email);

        return savedUser;
    }

    @Transactional(readOnly = true)
    public User login(String email, String password) {
        log.info("Login attempt: email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login failed: invalid email, email={}", email);
                    return new NoSuchElementException("invalid email or password");
                });

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Login failed: invalid password, email={}", email);
            throw new IllegalStateException("invalid email or password");
        }

        if (!user.isActive()) {
            log.warn("Login failed: user inactive, userId={}, email={}", user.getId(), email);
            throw new IllegalStateException("user is inactive");
        }

        log.info("Login successful: userId={}, email={}", user.getId(), email);

        return user;
    }

    @Transactional(readOnly = true)
    public String loginAndGenerateToken(String email, String password) {
        User user = login(email, password);
        return jwtService.generateToken(user);
    }
}