package by.bsuir.meetingroombooking.dto;

import by.bsuir.meetingroombooking.model.Role;

public record UserResponse(
        Long id,
        String name,
        String email,
        boolean active,
        Role role
) {
}
