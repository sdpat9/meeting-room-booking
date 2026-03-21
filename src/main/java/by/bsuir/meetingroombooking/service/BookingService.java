package by.bsuir.meetingroombooking.service;

import by.bsuir.meetingroombooking.model.Booking;
import by.bsuir.meetingroombooking.model.Room;
import by.bsuir.meetingroombooking.repository.BookingRepository;
import by.bsuir.meetingroombooking.repository.RoomRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookingService {
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public BookingService(RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public Room createRoom(String name, int capacity, boolean active) {
        Room room = new Room(name, capacity, active);
        roomRepository.save(room);
        return room;
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
    public Booking createBooking(Long roomId, Long userId, LocalDateTime start, LocalDateTime end) {
        Room room = getRoom(roomId);

        if (!room.isActive()) throw new IllegalStateException("room is inactive: " + roomId);

        Booking newBooking = new Booking(room, userId, start, end);

        boolean exists = bookingRepository.existsByRoom_IdAndStartBeforeAndEndAfter(
                roomId,
                end,
                start
        );

        if (exists) {
            throw new IllegalStateException("booking conflict for room " + roomId);
        }

        bookingRepository.save(newBooking);
        return newBooking;
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("booking not found: " + bookingId));
        booking.cancel();
    }

    @Transactional(readOnly = true)
    public Page<Booking> listBookingsForRoom(Long roomId, Pageable pageable) {
        return bookingRepository.findAllByRoomId(roomId, pageable);
    }

    @Transactional
    public void deactivateRoom(Long roomId) {
        Room room = getRoom(roomId);
        room.setActive(false);
    }

    @Transactional
    public List<Room> findAllAvailAbleRooms(LocalDateTime start, LocalDateTime end) {

        List<Room> allRooms = roomRepository.findAll();

        return allRooms.stream()
                .filter(Room::isActive)
                .filter(room ->
                        !bookingRepository.existsByRoom_IdAndStartBeforeAndEndAfter(
                                room.getId(),
                                end,
                                start
                        )
                )
                .toList();
    }
}