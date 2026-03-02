package repository;

import model.Room;

import java.util.Optional;
import java.util.List;

public interface RoomRepositry {
    Room save(Room room);
    Optional<Room> findById(Long id);
    List<Room> findAll();
}
