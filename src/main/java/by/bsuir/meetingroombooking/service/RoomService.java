package by.bsuir.meetingroombooking.service;

import by.bsuir.meetingroombooking.model.Room;
import by.bsuir.meetingroombooking.repository.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final AccessService accessService;

    public RoomService(RoomRepository roomRepository, AccessService accessService) {
        this.roomRepository = roomRepository;
        this.accessService = accessService;
    }

    @Transactional
    public Room createRoom(String name, int capacity, boolean active, Long adminId) {
        accessService.requireAdmin(adminId);

        Room room = new Room(name, capacity, active);
        return roomRepository.save(room);
    }

    @Transactional(readOnly = true)
    public Room getRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new NoSuchElementException("Room not found: " + roomId));
    }

    @Transactional(readOnly = true)
    public List<Room> listRooms() {
        return roomRepository.findAll();
    }

    @Transactional
    public Room updateRoom(Long roomId, String name, int capacity, boolean active, Long adminId) {
        accessService.requireAdmin(adminId);

        Room room = getRoom(roomId);
        room.update(name, capacity, active);
        return room;
    }

    @Transactional
    public void deactivateRoom(Long roomId, Long adminId) {
        accessService.requireAdmin(adminId);

        Room room = getRoom(roomId);
        room.setActive(false);
    }

    @Transactional(readOnly = true)
    public Page<Room> findAvailableRooms(
            LocalDateTime start,
            LocalDateTime end,
            Integer capacity,
            Pageable pageable
    ) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("start/end is required");
        }

        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("start must be before end");
        }

        if (capacity != null && capacity < 1) {
            throw new IllegalArgumentException("capacity must be at least 1");
        }

        if (pageable.getPageSize() > 50) {
            throw new IllegalArgumentException("page size must not exceed 50");
        }

        if (capacity == null) {
            return roomRepository.findAvailableRooms(start, end, pageable);
        }

        return roomRepository.findAvailableRooms(start, end, capacity, pageable);
    }
}