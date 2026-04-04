package by.bsuir.meetingroombooking.dto;

import java.time.LocalDateTime;
import by.bsuir.meetingroombooking.model.Status;

public record BookingResponse(
        Long id,
        Long roomId,
        Long userId,
        String title,
        int participantsCount,
        LocalDateTime start,
        LocalDateTime end,
        LocalDateTime createdAt,
        Status status
) {
}
