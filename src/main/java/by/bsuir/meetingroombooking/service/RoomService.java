package by.bsuir.meetingroombooking.service;

import by.bsuir.meetingroombooking.model.Room;
import by.bsuir.meetingroombooking.repository.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Comparator;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final AccessService accessService;
    private static final Logger log = LoggerFactory.getLogger(RoomService.class);

    public RoomService(RoomRepository roomRepository, AccessService accessService) {
        this.roomRepository = roomRepository;
        this.accessService = accessService;
    }

    @Transactional
    public Room createRoom(String name, int capacity, boolean active, Long adminId) {
        log.info("Creating room: name={}, capacity={}, active={}, adminId={}", name, capacity, active, adminId);

        accessService.requireAdmin(adminId);

        Room room = new Room(name, capacity, active);
        Room savedRoom = roomRepository.save(room);

        log.info("Room created successfully: roomId={}, name={}", savedRoom.getId(), savedRoom.getName());

        return savedRoom;
    }

    @Transactional(readOnly = true)
    public Page<Room> listRooms(
            Boolean active,
            String name,
            Pageable pageable
    ) {
        if (active != null && name != null) {
            return roomRepository.findByActiveAndNameContainingIgnoreCase(
                    active,
                    name,
                    pageable
            );
        }

        if (active != null) {
            return roomRepository.findByActive(active, pageable);
        }

        if (name != null) {
            return roomRepository.findByNameContainingIgnoreCase(name, pageable);
        }

        return roomRepository.findAll(pageable);
    }

    @Transactional
    public Room updateRoom(Long roomId, String name, int capacity, boolean active, Long adminId) {
        log.info("Updating room: roomId={}, name={}, capacity={}, active={}, adminId={}",
                roomId, name, capacity, active, adminId);

        accessService.requireAdmin(adminId);

        Room room = getRoom(roomId);
        room.update(name, capacity, active);

        log.info("Room updated successfully: roomId={}", roomId);

        return room;
    }

    @Transactional
    public void deactivateRoom(Long roomId, Long adminId) {
        log.info("Deactivating room: roomId={}, adminId={}", roomId, adminId);

        accessService.requireAdmin(adminId);

        Room room = getRoom(roomId);
        room.setActive(false);

        log.info("Room deactivated successfully: roomId={}", roomId);
    }

    @Transactional(readOnly = true)
    public Room getRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new NoSuchElementException("Room not found: " + roomId));
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

    @Transactional(readOnly = true)
    public Room recommendRoom(LocalDateTime start, LocalDateTime end, int participants) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("start/end is required");
        }

        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("start must be before end");
        }

        if (participants < 1) {
            throw new IllegalArgumentException("participants must be at least 1");
        }

        Page<Room> availableRooms = roomRepository.findAvailableRooms(
                start,
                end,
                participants,
                Pageable.unpaged()
        );

        return availableRooms.getContent()
                .stream()
                .min(Comparator.comparingInt(room -> room.getCapacity() - participants))
                .orElseThrow(() -> new NoSuchElementException("no suitable room found"));
    }
}