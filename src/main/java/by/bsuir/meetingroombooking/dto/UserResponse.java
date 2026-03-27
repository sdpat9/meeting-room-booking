package by.bsuir.meetingroombooking.dto;

public record UserResponse(
        Long id,
        String name,
        String email,
        boolean active
) {
}
