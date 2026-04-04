package by.bsuir.meetingroombooking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UpdateRoomRequest(
        @NotBlank(message = "room name must not be blank")
        String name,

        @Min(value = 1, message = "capacity must be at least 1")
        int capacity,

        boolean active
) {
}
