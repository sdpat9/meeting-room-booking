package by.bsuir.meetingroombooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateRoomRequest(
        @NotBlank(message = "Room name must not be blank")
        String name,

        @Min(value = 1, message = "Capacity must be at least 1")
        int capacity,
        boolean active
) {
}
