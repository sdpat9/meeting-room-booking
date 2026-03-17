package by.bsuir.meetingroombooking.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateBookingRequest(
        @NotNull(message = "Room id must not be null")
        Long roomId,

        @NotNull(message = "User id must not be null")
        Long userId,

        @NotNull(message = "Start time must not be null")
        LocalDateTime start,

        @NotNull(message = "End time must not be null")
        LocalDateTime end
) {
}
