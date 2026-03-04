package by.bsuir.meetingroombooking.repository;

import by.bsuir.meetingroombooking.model.Room;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryRoomRepository implements RoomRepositry {
    private final Map<Long, Room> storage = new HashMap<>();

    @Override
    public Room save(Room room) {
        storage.put(room.getId(), room);
        return room;
    }

    @Override
    public Optional<Room> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Room> findAll() {
        return new ArrayList<>(storage.values());
    }
}
