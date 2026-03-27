package by.bsuir.meetingroombooking.controller;

import by.bsuir.meetingroombooking.dto.CreateUserRequest;
import by.bsuir.meetingroombooking.dto.UserResponse;
import by.bsuir.meetingroombooking.mapper.UserMapper;
import by.bsuir.meetingroombooking.model.User;
import by.bsuir.meetingroombooking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final BookingService bookingService;

    public UserController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<UserResponse> listUsers() {
        return bookingService.listUsers()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        User user = bookingService.getUser(id);
        return UserMapper.toResponse(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest req) {
        User user = bookingService.addUser(
                req.name(),
                req.email(),
                req.active()
        );
        return UserMapper.toResponse(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateUser(@PathVariable Long id ) {
        bookingService.deactivateUser(id);
    }
}
