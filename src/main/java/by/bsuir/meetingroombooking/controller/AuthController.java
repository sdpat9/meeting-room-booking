package by.bsuir.meetingroombooking.controller;

import by.bsuir.meetingroombooking.dto.LoginRequest;
import by.bsuir.meetingroombooking.dto.RegisterRequest;
import by.bsuir.meetingroombooking.dto.UserResponse;
import by.bsuir.meetingroombooking.mapper.UserMapper;
import by.bsuir.meetingroombooking.model.User;
import by.bsuir.meetingroombooking.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest req) {
        User user = authService.register(
                req.name(),
                req.email(),
                req.password()
        );

        return UserMapper.toResponse(user);
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest req) {
        User user = authService.login(
                req.email(),
                req.password()
        );

        return UserMapper.toResponse(user);
    }
}