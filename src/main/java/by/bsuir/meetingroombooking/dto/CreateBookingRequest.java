package by.bsuir.meetingroombooking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

public record CreateBookingRequest(
        @NotNull(message = "Room id must not be null")
        Long roomId,

        @NotNull(message = "User id must not be null")
        Long userId,

        @NotNull(message = "Start time must not be null")
        LocalDateTime start,

        @NotNull(message = "End time must not be null")
        LocalDateTime end,

        @NotBlank(message = "Title must not be blank")
        String title,

        @Min(value = 1, message = "Participants count must be at least 1")
        int participantsCount
) {
}
