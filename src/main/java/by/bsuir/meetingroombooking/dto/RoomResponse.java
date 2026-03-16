package by.bsuir.meetingroombooking.dto;

public record RoomResponse(
        Long id,
        String name,
        int capacity,
        boolean active
) {
}
