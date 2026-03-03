package by.bsuir.meetingroombooking.repository;

import by.bsuir.meetingroombooking.model.Room;

import java.util.Optional;
import java.util.List;

public interface RoomRepositry {
    Room save(Room room);
    Optional<Room> findById(Long id);
    List<Room> findAll();
}
