package by.bsuir.meetingroombooking.controller;

import by.bsuir.meetingroombooking.dto.CreateUserRequest;
import by.bsuir.meetingroombooking.dto.UpdateUserRequest;
import by.bsuir.meetingroombooking.dto.UserResponse;
import by.bsuir.meetingroombooking.mapper.UserMapper;
import by.bsuir.meetingroombooking.model.User;
import by.bsuir.meetingroombooking.security.CustomUserDetails;
import by.bsuir.meetingroombooking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserResponse> listUsers() {
        return userService.listUsers()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        User user = userService.getUser(id);
        return UserMapper.toResponse(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody CreateUserRequest req) {
        User user = userService.createUser(
                req.name(),
                req.email(),
                req.password(),
                req.active(),
                req.role(),
                currentUser.getId()
        );
        return UserMapper.toResponse(user);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody UpdateUserRequest req
            ) {
        User user = userService.updateUser(
                id,
                req.name(),
                req.email(),
                req.active(),
                req.role(),
                currentUser.getId()
        );
        return UserMapper.toResponse(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateUser(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        userService.deactivateUser(id, currentUser.getId());
    }
}
