package by.bsuir.meetingroombooking.service;

import by.bsuir.meetingroombooking.model.Booking;
import by.bsuir.meetingroombooking.model.Room;
import by.bsuir.meetingroombooking.model.User;
import by.bsuir.meetingroombooking.repository.BookingRepository;
import by.bsuir.meetingroombooking.repository.RoomRepository;
import by.bsuir.meetingroombooking.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.*;
import java.time.Duration;

@Service
public class BookingService {
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public BookingService(RoomRepository roomRepository,
                          BookingRepository bookingRepository,
                          UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
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
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("user not found: " + userId));
    }

    @Transactional(readOnly = true)
    public List<Room> listRooms() {
        return roomRepository.findAll();
    }

    @Transactional
    public Booking createBooking(Long roomId, Long userId, LocalDateTime start, LocalDateTime end) {
        Room room = getRoom(roomId);
        User user = getUser(userId);

        if (!room.isActive()) {
            throw new IllegalStateException("room is inactive: " + roomId);
        }

        if (!user.isActive()) {
            throw new IllegalStateException("user is inactive: " + userId);
        }

        if (Duration.between(start, end).toHours() > 8) {
            throw new IllegalStateException("booking cannot exceed 8 hours");
        }

        Booking newBooking = new Booking(room, user, start, end);

        boolean roomConflict = bookingRepository.existsByRoom_IdAndStartBeforeAndEndAfter(
                roomId,
                end,
                start
        );

        boolean userConflict = bookingRepository.existsByUser_IdAndStartBeforeAndEndAfter(
                userId,
                end,
                start
        );

        if (userConflict) {
            throw new IllegalStateException("user already has booking in this time");
        }

        if (roomConflict) {
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
        return bookingRepository.findAllByRoom_Id(roomId, pageable);
    }

    @Transactional
    public void deactivateRoom(Long roomId) {
        Room room = getRoom(roomId);
        room.setActive(false);
    }

    @Transactional(readOnly = true)
    public List<Room> findAvailableRooms(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("start/end is required");
        }

        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("start must be before end");
        }

        return roomRepository.findAvailableRooms(start, end);
    }

    @Transactional
    public User createUser(String name, String email, boolean active) {
        User user = new User(name, email, active);
        userRepository.save(user);
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deactivateUser(Long userId) {
        User user = getUser(userId);
        user.setActive(false);
    }
}