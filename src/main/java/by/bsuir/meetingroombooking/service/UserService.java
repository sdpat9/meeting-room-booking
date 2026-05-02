package by.bsuir.meetingroombooking.service;

import by.bsuir.meetingroombooking.model.Role;
import by.bsuir.meetingroombooking.model.User;
import by.bsuir.meetingroombooking.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccessService accessService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       AccessService accessService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accessService = accessService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("user not found: " + userId));
    }

    @Transactional(readOnly = true)
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User createUser(String name, String email, String password, boolean active, Role role, Long adminId) {
        accessService.requireAdmin(adminId);

        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("email is already in use: " + email);
        }

        String encodedPassword = passwordEncoder.encode(password);

        User user = new User(name, email, encodedPassword, active, role);
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long userId, String name, String email, boolean active, Role role, Long adminId) {
        accessService.requireAdmin(adminId);

        User user = getUser(userId);

        if (userRepository.existsByEmailAndIdNot(email, userId)) {
            throw new IllegalStateException("email is already in use: " + email);
        }

        user.update(name, email, active, role);
        return user;
    }

    @Transactional
    public void deactivateUser(Long userId, Long adminId) {
        accessService.requireAdmin(adminId);

        User user = getUser(userId);
        user.setActive(false);
    }
}