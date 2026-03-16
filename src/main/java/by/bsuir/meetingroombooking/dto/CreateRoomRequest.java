package by.bsuir.meetingroombooking.dto;

public record CreateRoomRequest(
        String name,
        int capacity,
        boolean active
) {
}
