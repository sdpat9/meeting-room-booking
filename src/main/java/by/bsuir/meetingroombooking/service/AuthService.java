package by.bsuir.meetingroombooking.service;

import by.bsuir.meetingroombooking.model.Role;
import by.bsuir.meetingroombooking.model.User;
import by.bsuir.meetingroombooking.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import by.bsuir.meetingroombooking.security.JwtService;

import java.util.NoSuchElementException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public User register(String name, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("email is already in use: " + email);
        }

        String encodedPassword = passwordEncoder.encode(password);

        User user = new User(name, email, encodedPassword, true, Role.USER);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalStateException("invalid email or password");
        }

        if (!user.isActive()) {
            throw new IllegalStateException("user is inactive");
        }

        return user;
    }

    @Transactional(readOnly = true)
    public String loginAndGenerateToken(String email, String password) {
        User user = login(email, password);
        return jwtService.generateToken(user);
    }
}