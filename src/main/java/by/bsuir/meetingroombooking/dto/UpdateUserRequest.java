package by.bsuir.meetingroombooking.dto;

import by.bsuir.meetingroombooking.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;

public record UpdateUserRequest(
        @NotBlank(message = "name must not be blank")
        String name,

        @NotBlank(message = "email must not be blank")
        @Email(message = "email must be valid")
        String email,

        boolean active,

        @NotNull(message = "Role must not be null")
        Role role
) {
}
