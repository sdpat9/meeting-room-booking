package by.bsuir.meetingroombooking.dto;

import java.time.LocalDateTime;

public record CreateBookingRequest(
        Long roomId,
        Long userId,
        LocalDateTime start,
        LocalDateTime end
) {
}
