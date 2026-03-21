package by.bsuir.meetingroombooking.mapper;

import by.bsuir.meetingroombooking.dto.RoomResponse;
import by.bsuir.meetingroombooking.model.Room;

public class RoomMapper {
    public static RoomResponse toResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getName(),
                room.getCapacity(),
                room.isActive()
        );
    }
}
