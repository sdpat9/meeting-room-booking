package by.bsuir.meetingroombooking.dto;

import by.bsuir.meetingroombooking.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank(message = "User name must not be blank")
        String name,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be valid")
        String email,

        boolean active,

        @NotNull(message = "Role must not be null")
        Role role
) {
}