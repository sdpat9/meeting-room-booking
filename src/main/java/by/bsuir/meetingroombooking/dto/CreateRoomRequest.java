package by.bsuir.meetingroombooking.dto;

public record CreateRoomRequest(
        Long id,
        String name,
        int capacity,
        boolean active
) {
}
