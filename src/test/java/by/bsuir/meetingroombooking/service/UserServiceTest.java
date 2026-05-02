package by.bsuir.meetingroombooking.service;

import by.bsuir.meetingroombooking.model.Role;
import by.bsuir.meetingroombooking.model.User;
import by.bsuir.meetingroombooking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private AccessService accessService;
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        accessService = mock(AccessService.class);
        passwordEncoder = mock(PasswordEncoder.class);

        userService = new UserService(userRepository, accessService, passwordEncoder);
    }

    @Test
    void createUser_duplicateEmail_throwsException() {
        when(userRepository.existsByEmail("sergey@example.com"))
                .thenReturn(true);

        assertThrows(IllegalStateException.class, () ->
                userService.createUser(
                        "Sergey",
                        "sergey@example.com",
                        "password123",
                        true,
                        Role.USER,
                        1L
                )
        );

        verify(accessService).requireAdmin(1L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_admin_success() {
        User user = new User("Sergey", "sergey@example.com", "encodedPassword",true, Role.USER);

        when(userRepository.existsByEmail("sergey@example.com"))
                .thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        User result = userService.createUser(
                "Sergey",
                "sergey@example.com",
                "password123",
                true,
                Role.USER,
                1L
        );

        assertEquals("Sergey", result.getName());
        assertEquals("sergey@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(Role.USER, result.getRole());

        verify(accessService).requireAdmin(1L);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_nonAdmin_forbidden() {
        doThrow(new IllegalStateException("admin role is required"))
                .when(accessService)
                .requireAdmin(2L);

        assertThrows(IllegalStateException.class, () ->
                userService.updateUser(
                        1L,
                        "New Name",
                        "new@example.com",
                        true,
                        Role.USER,
                        2L
                )
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_duplicateEmail_throwsException() {
        User user = new User("Sergey", "sergey@example.com", "encodedPassword",true, Role.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("new@example.com", 1L))
                .thenReturn(true);

        assertThrows(IllegalStateException.class, () ->
                userService.updateUser(
                        1L,
                        "Sergey",
                        "new@example.com",
                        true,
                        Role.USER,
                        1L
                )
        );

        verify(accessService).requireAdmin(1L);
    }

    @Test
    void updateUser_admin_success() {
        User user = new User("Sergey", "sergey@example.com", "encodedPassword",true, Role.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("new@example.com", 1L))
                .thenReturn(false);

        User result = userService.updateUser(
                1L,
                "New Name",
                "new@example.com",
                false,
                Role.ADMIN,
                1L
        );

        assertEquals("New Name", result.getName());
        assertEquals("new@example.com", result.getEmail());
        assertFalse(result.isActive());
        assertEquals(Role.ADMIN, result.getRole());
    }
}